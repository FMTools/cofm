package collab.fm.mining.constraint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import libsvm.api.*;

import collab.fm.mining.opt.Domain;
import collab.fm.mining.opt.GeneticOptimizer;
import collab.fm.mining.opt.Optimizable;
import collab.fm.mining.opt.Optimizer;
import collab.fm.mining.opt.Solution;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.persist.relation.RelationType;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.bean.protocol.op.VoteAddBinRelationRequest;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.util.exception.SvmException;

/**
 * The Support Vector Machine (SVM) classification consists of 4 steps:
 *   1. Output training data and test data
 *   2. Scale the data
 *   3. Train
 *   4. Predict (by machine) and Check (by human)
 * The code is organized in the same way. Here are some additional notes:
 *    The training data is incremental, that is, the correct predictions (confirmed by human)
 * will be append to training data.
 *    The test data can be incremental OR fixed, that is, the correct predictions
 * (which is made on test data, of course) are also append to test data, or the user
 * can disallow the appending so the test data is fixed.
 *    
 *    Output the test data ---
 * First, the attributes relating to constraints (such as "requireOutsider") is set to UNKNOWN
 * if its original value is NONE (-1) and its class is NO_CONSTRAINT. 
 * Besides, there are 3 modes for outputting:
 *    1) Default. Nothing more.
 *    2) Blank. All existing constraints in the test FM are REMOVED from the data (but not from
 *    the original FM, of course). The total number of pairs will not change.
 *    3) No_Con. All constrained-pairs are left out of the test data, so only the NO_CONSTRAINT pairs
 *    is used for testing.
 *    
 *    Output the training data ---
 * You can also use the TEST FMs to get some training data: that is, all existing constraints
 * in the test FMs are treated as training data. 
 * 
 * @author Li Yi
 *
 */
public class SVM implements Optimizable {

	static Logger logger = Logger.getLogger(SVM.class);
	
	public static final int MAX_PREDICTION_NUM = 20;
	public static final String TRAINING_FILE = "mining/cons_svm_train";
	public static final String SCALED_FILE_SUFFIX = ".scale";
	public static final String SCALE_RANGE_FILE = "mining/cons_svm_scale_range";
	public static final String MODEL_FILE = "mining/cons_svm_model";
	public static final String TEST_FILE = "mining/cons_svm_test";
	public static final String PREDICT_RESULT_FILE = "mining/cons_svm_predict";
	
	// ----------- Parameters ------------
	// We can adjust these parameters to find optimized values.
	
	// Scale the data to [-1, 1]
	public static final String ARG_SCALE_TRAINING = 
		"-l -1 -u 1 -s " + SCALE_RANGE_FILE + " " + TRAINING_FILE;
	
	public static final String ARG_SCALE_TEST = 
		"-r " + SCALE_RANGE_FILE + " " + TEST_FILE;
	
	// Training: Default gamma = 1 / number of attributes
	public static final double DEFAULT_GAMMA = (double) 1 / FeaturePair.NUM_ATTRIBUTES;
	public static double gamma = DEFAULT_GAMMA;
	public static int reqWeight = 5;   // Bonus for finding a "require" constraint.
	public static int excWeight = 5;   // Bonus for finding a "exclude" constraint.
	public static int cvFold = 4;    // The fold of CV
	
	// Store the cross-validation result.
	public SVM.CV cvResult;
	
	// ------------ Step 1. Output data -------------
	private String formatPair(FeaturePair pair) {
		// Format as LIBSVM required
		return pair.getLabel()
			   + " 1:" + pair.getSimilarity()
			   + " 2:" + pair.getParental()
			   + " 3:" + pair.getSibling() 
			   + " 4:" + pair.getNumMandatory() 
			   + " 5:" + pair.getRequireOut() 
			   + " 6:" + pair.getExcludeOut() 
		//	   + " 7:" + pair.getParentRequireOut()
		//	   + " 8:" + pair.getParentExcludeOut()
			   ;
	}
	
	private void dumpTrainingSet(List<Model> trainFMs, List<Model> testFMs) {
		logger.info("*** Dump Training Set.");
		try {
			BufferedWriter tf = new BufferedWriter(new FileWriter(SVM.TRAINING_FILE));
			PairStats stats = new PairStats();
			
			for (Model trainFM: trainFMs) {
				stats.addPair(dumpModel(tf, trainFM, MODE_TRAIN_ALL));
			}
			if (testFMs != null) {
				for (Model testFM: testFMs) {
					stats.addPair(dumpModel(tf, testFM, MODE_TRAIN_ONLY_CON));
				}
			}
			
			tf.close();
			logger.info("*** Dump Training Set END.");
			logger.info(stats.toString());
			
		} catch (IOException e) {
			logger.error("Cannot open data file", e);
		}
	}
	
	private static final int MODE_TEST_ALL = 0;
	private static final int MODE_TEST_BLANK = 1;
	private static final int MODE_TEST_NO_CON = 2;
	private static final int MODE_TRAIN_ALL = 3; 
	private static final int MODE_TRAIN_ONLY_CON = 4;
	private static final String[] modeName = {
		"Test_All", "Test_Blank", "Test_No_Con", "Train_All", "Train_Only_Con"
	};
	
	private List<FeaturePair> dumpModel(BufferedWriter out, Model model, int mode) {
		Entity[] features = model.getEntities().toArray(new Entity[0]);
		// Random shuffle the features.
		Collections.shuffle(Arrays.asList(features));

		int numPair = 0, numSim = 0, numRequire = 0, numExclude = 0;
		List<FeaturePair> pairs = new ArrayList<FeaturePair>();
		
		for (int i = 0; i < features.length; i++) {
			for (int j = i + 1; j < features.length; j++) {
				FeaturePair pair = new FeaturePair(features[i], features[j]);
				boolean keepPair = true;
				if (mode == MODE_TEST_BLANK) {
					// Set all pair to Non-constraint, and set all constraint-related
					// attributes to UNKNOWN
					pair.setLabel(FeaturePair.NO_CONSTRAINT);
					pair.setRequireOut(FeaturePair.UNKNOWN);
					pair.setExcludeOut(FeaturePair.UNKNOWN);
				} else {
					if (pair.getLabel() == FeaturePair.NO_CONSTRAINT 
							|| pair.getLabel() == FeaturePair.UNKNOWN) {
						if (mode == MODE_TEST_NO_CON) {
							if (pair.getRequireOut() == FeaturePair.NO){
								pair.setRequireOut(FeaturePair.UNKNOWN);
							}
							if (pair.getExcludeOut() == FeaturePair.NO) {
								pair.setExcludeOut(FeaturePair.UNKNOWN);
							}
						} else if (mode == MODE_TRAIN_ONLY_CON) {
							keepPair = false; // Skip non-constraint pairs in training set
						}
					} else if (mode == MODE_TEST_NO_CON) {
						keepPair = false;  // Skip constraint-pairs in test set
					}
				}
				if (!keepPair) {
					continue;
				}
				pairs.add(pair);
				try {
					out.write(formatPair(pair) + "\n");
				} catch (IOException e) {
					logger.warn("Cannot write pair.", e);
				}
				if (pair.getSimilarity() > 0.0f) {
					numSim++;
				}
				if (pair.getLabel() == FeaturePair.REQUIRE) {
					numRequire++;
				} else if (pair.getLabel() == FeaturePair.EXCLUDE) {
					numExclude++;
				}
				numPair++;
			}
		}

		logger.info("Feature Model: '" + model.getName() + "': " 
				+ modeName[mode] + ", "
				+ features.length + " features, " 
				+ numPair + " pairs, "
				+ numRequire + " require-pairs, " + numExclude
				+ " exclude-pairs, " + numSim + " pairs of similar features.");
		return pairs;
	}
	
	// ------------ Step 2. Scale data ------------
	private void scaleTrainingSet() {
		logger.info("*** Scale Training Set to [-1, 1]");
		this.scaleData(SVM.TRAINING_FILE + SVM.SCALED_FILE_SUFFIX,
				SVM.ARG_SCALE_TRAINING);
		logger.info("*** Scale END.");
	}
	
	private void scaleTestSet() {
		logger.info("*** Scale Test Set to [-1, 1]");
		this.scaleData(TEST_FILE + SCALED_FILE_SUFFIX, ARG_SCALE_TEST);
		logger.info("*** Scale END.");
	}
	
	private boolean scaleData(String outputFile, String parameters) {	
		PrintStream stdout = System.out;
		try {
			// First, we need to redirect the System.out to a file
			PrintStream scaleFile = new PrintStream(outputFile);
			System.setOut(scaleFile);
			
			// Call the svm_scale.run() with arguments
			svm_scale s = new svm_scale();
			s.run(parameters.split("\\s"));
			
			scaleFile.close();
			
			return true;
		} catch (FileNotFoundException e) {
			logger.error("IO error.", e);
			return false;
		} catch (IOException e) {
			logger.error("IO error.", e);
			return false;
		} catch (SvmException e) {
			logger.error("Scale failed.", e);
			return false;
		} finally {
			System.setOut(stdout);
		}
	}
	
	// ------------ Step 3. Training ------------
	
	// ------------ Step 4. Predict and check -------------
	
	// ------------ Main --------------
	
	
	// Train and then do cross-validation (CV)
	private boolean trainWithCV() {
		
		svm_train t = new svm_train();
		try {
			String arg_training = 
				"-q -g " + gamma
				+ " -w1 " + reqWeight 
				+ " -w2 " + excWeight 
				+ " -v " + cvFold + " " + TRAINING_FILE + SCALED_FILE_SUFFIX;
			t.run(arg_training.split("\\s"), cvResult);
			return true;
		} catch (IOException e) {
			logger.error("IO error.", e);
			return false;
		} catch (SvmException e) {
			logger.error("Training error.", e);
			return false;
		}
	}
	
	private boolean train() {
		svm_train t = new svm_train();
		try {
			String arg_training = 
				"-q -g " + gamma
				+ " -w1 " + reqWeight 
				+ " -w2 " + excWeight 
				+ " " + TRAINING_FILE + SCALED_FILE_SUFFIX
				+ " " + MODEL_FILE;
			t.run(arg_training.split("\\s"), null);
			return true;
		} catch (IOException e) {
			logger.error("IO error.", e);
			return false;
		} catch (SvmException e) {
			logger.error("Training error.", e);
			return false;
		}
	}
	

	
	private boolean predict(String outputFile) {
			String argPredict = TEST_FILE + SCALED_FILE_SUFFIX +
				" " + MODEL_FILE + " " + outputFile;
			try {
				svm_predict.run(argPredict.split("\\s"));
				return true;
			} catch (IOException e) {
				logger.error("IO error.", e);
				return false;
			} catch (SvmException e) {
				logger.error("Predict error.", e);
				return false;
			}
	}
	
	
	
	
	


	// Cost = 100 - Accuracy
	public double computeCost(Solution s) {
		SVM.gamma = s.parts[0].value;
		SVM.reqWeight = Double.valueOf(s.parts[1].value).intValue();
		SVM.excWeight = Double.valueOf(s.parts[2].value).intValue();
		
		this.trainWithCV();
		
		return 100.0 - this.cvResult.accuracy;
	}

	// Solution = [gamma, reqWeight, excWeight]
	public Solution defineSolution() {
		Domain[] parts = new Domain[] {
			new Domain(false, SVM.DEFAULT_GAMMA / 2, SVM.DEFAULT_GAMMA * 2, 0.02, Double.NaN),
			new Domain(true, 5, 30, 1, Double.NaN),
			new Domain(true, 5, 30, 1, Double.NaN) 
		};
		Solution s = new Solution();
		s.parts = parts;
		return s;
	}
	
	public Solution optimizeParameters(Model model) {
		this.cvResult = new SVM.CV();
			
		this.scaleTrainingSet();

		logger.info("*** Optimizing Parameters");
		long startTime = System.currentTimeMillis();
		GeneticOptimizer o = new GeneticOptimizer();
		o.population = 25;
		o.generation = 80;
		Solution best = o.optimize(this);
		long elapsedTime = System.currentTimeMillis() - startTime;
		logger.info("*** Optimizing over, time elapsed: "
				+ (elapsedTime / 1000.0f) + " seconds.");
		logger.info("*** Optimized Parameter:" + "\n\tgamma = "
				+ best.parts[0].value + "\n\tweight of require class = "
				+ best.parts[1].value + "\n\tweight of exclude class = "
				+ best.parts[2].value + "\nAccuracy = " + (100 - best.cost)
				+ "%");
		return best;
			
	}
	
	public void trainModel() {
		this.scaleTrainingSet();
		this.train();
	}
	
	public static final int NO_CONSTRAINT = 0;
	public static final int REQUIRES = 1;
	public static final int REQUIRED_BY = 2;
	public static final int MUTUAL_REQUIRE = 3;
	public static final int EXCLUDE = 4;
	
	private void removeConstraint(FeaturePair pair) {
		// Set the pair to "Non-Constrained"
		Relation r = pair.getConstraint();
		if (r != null) {
			VoteAddBinRelationRequest req = new VoteAddBinRelationRequest();
			req.setModelId(pair.getModel().getId());
			req.setName(Resources.REQ_VA_RELATION_BIN);
			req.setYes(false);
			req.setRelationId(r.getId());
			
			// Force all voters to vote NO on this relation, thus to remove it from the FM.
			for (Long uid: r.getVote().getSupporters()) {
				req.setRequesterId(uid);
				
				try {
					req.process(new ResponseGroup());
				} catch (ItemPersistenceException e) {
					logger.warn("Remove constraints fails", e);
				} catch (StaleDataException e) {
					logger.warn("Remove constraints fails", e);
				} catch (InvalidOperationException e) {
					logger.warn("Remove constraints fails", e);
				}
			}
		}
	}
	
	private void persistConstraint(FeaturePair pair, int type) {
		VoteAddBinRelationRequest req = new VoteAddBinRelationRequest();
		req.setModelId(pair.getModel().getId());
		req.setName(Resources.REQ_VA_RELATION_BIN);
		if (type == REQUIRED_BY) {
			req.setSourceId(pair.getSecond().getId());
			req.setTargetId(pair.getFirst().getId());
		} else {
			req.setSourceId(pair.getFirst().getId());
			req.setTargetId(pair.getSecond().getId());
		}
		req.setYes(true);
		// Get constraint type
		String typeName = (type == EXCLUDE ? Resources.BIN_REL_EXCLUDES : Resources.BIN_REL_REQUIRES);
		for (RelationType rt: pair.getModel().getRelationTypes()) {
			if (rt.getTypeName().equals(typeName)) {
				req.setTypeId(rt.getId());
				break;
			}
		}
		try {
			req.setRequesterId(DaoUtil.getUserDao().getByName("admin").getId());
		
			req.process(new ResponseGroup());
			
			if (type == MUTUAL_REQUIRE) {
				req.setSourceId(pair.getSecond().getId());
				req.setTargetId(pair.getFirst().getId());
				req.process(new ResponseGroup());
			}
			
		} catch (ItemPersistenceException e) {
			logger.warn("Persist constraint fails.", e);
		} catch (StaleDataException e) {
			logger.warn("Persist constraint fails.", e);
		} catch (InvalidOperationException e) {
			logger.warn("Persist constraint fails.", e);
		}
	}
	
	public void predictAndCheck(Model test) {
		List<FeaturePair> testPairs = null;
		int pairIndex = 0;
		if ((testPairs = this.dumpDataFile(test, SVM.TEST_FILE)) != null) {
			this.scaleTestSet();
			this.predict(SVM.PREDICT_RESULT_FILE);
			try {
				logger.info("\nPrediction checking begin.");
				BufferedReader result = new BufferedReader(new FileReader(SVM.PREDICT_RESULT_FILE));
				BufferedWriter tf = new BufferedWriter(new FileWriter(SVM.TRAINING_FILE, true)); // true = append mode
					
				String s;
				int numCorrect = 0, numPrediction = 0;
				while (numPrediction < MAX_PREDICTION_NUM && (s = result.readLine()) != null) {
					int label = Float.valueOf(s).intValue();
					FeaturePair cur = testPairs.get(pairIndex++);
					if (label != cur.getLabel()) {
						// If this label is different from the original one, it is 
						// a prediction needed checking.
						numPrediction++;
						// Print the prediction to user
						System.out.print("\n" + cur.getPairInfo());
						System.out.println("-->Prediction is: " +
								(label == FeaturePair.EXCLUDE ? "EXCLUDE" : 
									(label == FeaturePair.REQUIRE ? "REQUIRE" : "NOT_CONSTRAINED")));
						System.out.print("Input your answer ("
								+ NO_CONSTRAINT + " = Not-constrained, "
								+ REQUIRES + " = Requires, "
								+ REQUIRED_BY + " = Required_by, "
								+ MUTUAL_REQUIRE + " = Mutual_require, "
								+ EXCLUDE + " = Exclude): ");
						// Get answer from user
						Scanner scanner = new Scanner(System.in);
						int answer = scanner.nextInt();
						int type = 0;
						if (answer == NO_CONSTRAINT) {
							type = FeaturePair.NO_CONSTRAINT;
						} else if (answer == EXCLUDE) {
							type = FeaturePair.EXCLUDE;
						} else {
							type = FeaturePair.REQUIRE;
						}
						cur.setLabel(type);
						if (type == label) {
							numCorrect++;
						}
						// Add the pair to the training set.
						tf.write(this.formatPair(cur) + "\n");
						System.out.println("The answer has been added to traning set.");
						// if the answer type is different from cur pair (i.e. the test FM), 
						// we need to apply the answer to the test FM
						if (type != cur.getLabel()) {
							if (type == FeaturePair.EXCLUDE || type == FeaturePair.REQUIRE) {
								persistConstraint(cur, answer);
								System.out.println("The constraint has been added to the test FM.");
							} else if (type == FeaturePair.NO_CONSTRAINT) {
								removeConstraint(cur);
								System.out.println("The constraint has been removed from the test FM.");
							}
						}
					}
				}
				logger.info("\nPrediction checking end." +
						"\nAccuracy = " + (100.0f * numCorrect / numPrediction) + "% (" +
						numCorrect + "/" + numPrediction + ")");
				result.close();
				tf.close();
			} catch (FileNotFoundException e) {
				logger.warn("Cannot open prediction file.", e);
			} catch (IOException e) {
				logger.warn("Cannot read prediction file.", e);
			}
			
		}
	}
	
	public static void main(String[] argv) throws IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();;
		try {
			session.beginTransaction();
			
			Model model = DaoUtil.getModelDao().getByName("Media Player");
			Model testModel = DaoUtil.getModelDao().getByName("IM");
			if (model == null) {
				logger.warn("No such model");
			} else {
				SVM svm = new SVM();
				// Steps:
				//   1. optimizeParameters with training FM.
				//   2. trainModel with training FM and predictAndCheck with test FM
				//   3. trainModel with null parameter and predictAndCheck with test FM
				svm.attrClassStats(svm.dumpTrainingSet(model));
//				for (int i = 0; i < 3; i++) {
//				Solution best = svm.optimizeParameters(model);
//				}
				//SVM.gamma = best.parts[0].value;
				//SVM.reqWeight = Double.valueOf(best.parts[1].value).intValue();
				//SVM.excWeight = Double.valueOf(best.parts[2].value).intValue();
				
				SVM.gamma = 0.14490230819623984;
				SVM.reqWeight = 9;
				SVM.excWeight = 16;
				
				int again = 0;
				do {
					svm.trainModel();
					svm.predictAndCheck(testModel);
					
					System.out.print("\nPrediction end. Do it again? (0 = NO, 1 = YES):");
					Scanner scanner = new Scanner(System.in);
					again = scanner.nextInt();
				} while (again == 1);
			}
		
			session.getTransaction().commit();
			
		} catch (HibernateException he) {
			session.getTransaction().rollback();
			logger.error("Database error.", he);
			session.close();
		} catch (ItemPersistenceException e) {
			logger.error("Database error.", e);
			session.close();
		} catch (StaleDataException e) {
			logger.error("Database error.", e);
			session.close();
		} 		
	}

	private static class PairStats {
		
		private int[] simLow = {0, 0, 0}; // similarity low (<0.3)
		private int[] simMed = {0, 0, 0}; // similarity medium (0.3 - 0.6)
		private int[] simHigh = {0, 0, 0}; // similarity high (>0.6)
		
		private int[] reqOutNo = {0, 0, 0}; // require outsider = NO
		private int[] reqOutYes = {0, 0, 0}; // require outsider = 1 or 2
		
		private int[] excOutNo = {0, 0, 0}; // exclude outsider = NO
		private int[] excOutYes = {0, 0, 0}; // exclude outsider = 1 or 2
		
		public String toString() {
			return String.format("*** Attribute distribution over class.\n"
					+ "%10s%10s%10s%10s\n"
					+ "-------------------------------------------------------\n"
					+ "%10s%10d%10d%10d\n"   // Low similarity
					+ "%10s%10d%10d%10d\n"   // Medium 
					+ "%10s%10d%10d%10d\n"   // High
					+ "%10s%10d%10d%10d\n"   // No require out
					+ "%10s%10d%10d%10d\n"   // Has 
					+ "%10s%10d%10d%10d\n"   // No exclude out
					+ "%10s%10d%10d%10d",    // Has
					" ", "NO_CONS", "REQUIRE", "EXCLUDE",  // The Table Head
					"Low_Sim", simLow[0], simLow[1], simLow[2],
					"Med_Sim", simMed[0], simMed[1], simMed[2],
					"High_Sim", simHigh[0], simHigh[1], simHigh[2],
					"No_Req_Out", reqOutNo[0], reqOutNo[1], reqOutNo[2],
					"Has", reqOutYes[0], reqOutYes[1], reqOutYes[2],
					"No_Exc_Out", excOutNo[0], excOutNo[1], excOutNo[2],
					"Has", excOutYes[0], excOutYes[1], excOutYes[2]
					);
		}
		
		// show stats about attributes and class correspondence
		public void addPair(List<FeaturePair> pairs) {
			for (FeaturePair pair: pairs) {
				int index = (pair.getLabel() == FeaturePair.NO_CONSTRAINT ? 0 : 
					(pair.getLabel() == FeaturePair.REQUIRE ? 1 : 2));
				if (pair.getSimilarity() <= 0.3f) {
					simLow[index]++;
				} else if (pair.getSimilarity() > 0.3f && pair.getSimilarity() < 0.6f) {
					simMed[index]++;
				} else {
					simHigh[index]++;
				}
				if (pair.getRequireOut() == FeaturePair.NO) {
					reqOutNo[index]++;
				} else if (pair.getRequireOut() >= 1) {
					reqOutYes[index]++;
				}
				if (pair.getExcludeOut() == FeaturePair.NO) {
					excOutNo[index]++;
				} else if (pair.getExcludeOut() >= 1) {
					excOutYes[index]++;
				}
			}
		}
	}
	
	// result for cross-validation (CV)
	public static class CV {
		public double accuracy;
		public double meanSquareError;
		public double squareCoefficient;
	}
}
