package collab.fm.mining.constraint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import libsvm.api.*;

import collab.fm.mining.constraint.filter.*;
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
	
	private List<PairFilter> filters = new ArrayList<PairFilter>();
	
	public static final int MAX_PREDICTION_NUM = 20;
	public static final String TRAINING_FILE = "D:/log/cofm/mining/cons_svm_train";
	public static final String SCALED_FILE_SUFFIX = ".scale";
	public static final String SCALE_RANGE_FILE = "D:/log/cofm/mining/cons_svm_scale_range";
	public static final String MODEL_FILE = "D:/log/cofm/mining/cons_svm_model";
	public static final String TEST_FILE = "D:/log/cofm/mining/cons_svm_test";
	public static final String PREDICT_RESULT_FILE = "D:/log/cofm/mining/cons_svm_predict";
	
	// ----------- Arguments for calling LIBSVM ------------
	// Scale the data to [-1, 1]
	public static final String ARG_SCALE_TRAINING = 
		"-l -1 -u 1 -s " + SCALE_RANGE_FILE + " " + TRAINING_FILE;
	
	public static final String ARG_SCALE_TEST = 
		"-r " + SCALE_RANGE_FILE + " " + TEST_FILE;
	
	// Parameters for SVM algorithm
	private int numDataAttr;
	private double gamma, gammaLo, gammaHi, gammaStep;
	private int reqWeight, reqLo, reqHi, reqStep;
	private int excWeight, excLo, excHi, excStep;
	private int cvFold;
	
	// Store the cross-validation result.
	public SVM.CV cvResult;
	
	private BufferedReader nounDictFile;
	
	private Properties cfg;
	
	public SVM(Properties cfg) {
		this.cfg = cfg;
		buildFilterChain();
	}
	
	private void buildFilterChain() {
		filters.clear();
		filters.add(new ConstraintsOnlyFilter());
		filters.add(new SimilarityFilter(0.0));
		filters.add(new CrossTreeOnlyFilter());
		
		String listNounFile = cfg.getProperty(SVM.KEY_LIST_NOUN);
		if (listNounFile != null && !listNounFile.isEmpty()) {
			filters.add(new ListNounFilter(listNounFile));
		}
	}
	
	// ------------ Step 1. Output data -------------
	private boolean keepPair(FeaturePair pair, int mode) {
		for (int i = 0; i < filters.size(); i++) {
			if (!filters.get(i).keepPair(pair, mode)) {
				return false;
			}
		}
		return true;
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
			
			// Do scaling
			this.scaleTrainingSet();
			
		} catch (IOException e) {
			logger.error("Cannot open data file", e);
		}
	}
	
	private List<FeaturePair> dumpTestSet(List<Model> testFMs, int mode) {
		List<FeaturePair> testPairs = new ArrayList<FeaturePair>();
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(SVM.TEST_FILE));
			logger.info("*** Dump Test Set.");
			for (Model tfm: testFMs) {
				testPairs.addAll(this.dumpModel(out, tfm, mode));
			}
			out.close();
			logger.info("*** Dump Test Set END.");
			this.scaleTestSet();
		} catch (IOException e) {
			logger.warn("Cannot dump test set.", e);
		}
		
		return testPairs;
	}
	
	
	public static final int MODE_TEST_ALL = 0;
	public static final int MODE_TEST_BLANK = 1;
	public static final int MODE_TRAIN_ALL = 2; 
	public static final int MODE_TRAIN_ONLY_CON = 3;
	public static final String[] modeName = {
		"Test_All", "Test_Blank", "Train_All", "Train_Only_Con"
	};
	
	private List<FeaturePair> dumpModel(BufferedWriter out, Model model, int mode) {
		FeaturePair.clearFeatureSet();
		Entity[] features = model.getEntities().toArray(new Entity[0]);
		// Random shuffle the features.
		Collections.shuffle(Arrays.asList(features));

		int numSim = 0, numRequire = 0, numExclude = 0;
		List<FeaturePair> pairs = new ArrayList<FeaturePair>();
		
		for (int i = 0; i < features.length; i++) {
			for (int j = i + 1; j < features.length; j++) {
				FeaturePair pair = new FeaturePair(features[i], features[j]);
				if (mode == MODE_TEST_BLANK) {
					// Set all pair to Non-constraint, and set all constraint-related
					// attributes to UNKNOWN
					pair.setLabel(FeaturePair.NO_CONSTRAINT);
					pair.setRequireOut(FeaturePair.UNKNOWN);
					pair.setExcludeOut(FeaturePair.UNKNOWN);
				} else if (mode == MODE_TEST_ALL) {
					// Set "No" to "Unknown" in test set
					if (pair.getRequireOut() == FeaturePair.NO){
						pair.setRequireOut(FeaturePair.UNKNOWN);
					}
					if (pair.getExcludeOut() == FeaturePair.NO) {
						pair.setExcludeOut(FeaturePair.UNKNOWN);
					}
				}
				pairs.add(pair);
			}
		}

		// Complete the pair similarity and output them.
		List<FeaturePair> kept = new ArrayList<FeaturePair>();
		for (FeaturePair p: pairs) {
			p.updateTextSimilarity();
			if (this.keepPair(p, mode)) {
				kept.add(p);
				if (p.getLabel() == FeaturePair.REQUIRE) {
					numRequire++;
				} else if (p.getLabel() == FeaturePair.EXCLUDE) {
					numExclude++;
				}
				if (p.getTotalSim() > 0.0) {
					numSim++;
				}
				try {
					out.write(SVMDataFormatter.format(p.getLabel(), p) + "\n");
				} catch (IOException e) {
					logger.warn("Cannot write pair.", e);
				}
			}
		}
		logger.info("Feature Model: '" + model.getName() + "': " 
				+ modeName[mode] + ", "
				+ features.length + " features, " 
				+ kept.size() + " valid pairs, including "
				+ numRequire + " require-pairs, " + numExclude
				+ " exclude-pairs, and " + numSim + " pairs of similar features.");
		return kept;
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
	
	private boolean trainWithoutCV() {
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
	
	// Cost = 100 - Accuracy
	public double computeCost(Solution s) {
		gamma = s.parts[0].value;
		reqWeight = Double.valueOf(s.parts[1].value).intValue();
		excWeight = Double.valueOf(s.parts[2].value).intValue();
		
		this.trainWithCV();
		
		return 100.0 - this.cvResult.accuracy;
	}

	
	
	// Solution = [gamma, reqWeight, excWeight]
	public Solution defineSolution() {
		Domain[] parts = new Domain[] {
			new Domain(false, gammaLo, gammaHi, gammaStep, Double.NaN),
			new Domain(true, reqLo, reqHi, reqStep, Double.NaN),
			new Domain(true, excLo, excHi, excStep, Double.NaN) 
		};
		Solution s = new Solution();
		s.parts = parts;
		return s;
	}
	
	// ------------ Step 4. Predict and check -------------
	private boolean predict() {
		String argPredict = TEST_FILE + SCALED_FILE_SUFFIX +
			" " + MODEL_FILE + " " + PREDICT_RESULT_FILE;
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
	
	private List<Model> getFMs(Properties cfg, String key) {
		String[] names = cfg.getProperty(key).split(";");
		List<Model> models = new ArrayList<Model>(names.length);
		for (String s: names) {
			try {
				Model m = DaoUtil.getModelDao().getByName(s);
				if (m != null) {
					models.add(m);
				}
			} catch (ItemPersistenceException e) {
				logger.warn("Cannot get model: " + s, e);
			} catch (StaleDataException e) {
				logger.warn("Cannot get model: " + s, e);
			}
		}
		return models;
	}
	
	// ------------ Main --------------
	public static final String KEY_TRAIN_FM = "svm.train.fm";
	public static final String KEY_TEST_FM = "svm.test.fm";
	public static final String KEY_GAMMA = "svm.gamma";
	public static final String KEY_WREQ = "svm.wreq";
	public static final String KEY_WEXC = "svm.wexc";
	public static final String KEY_CV = "svm.cv";
	public static final String KEY_OPT_PASS = "svm.opt.pass";
	public static final String KEY_POPSIZE = "svm.opt.gen.popsize";
	public static final String KEY_ITER = "svm.opt.gen.iter";
	public static final String KEY_TOP = "svm.opt.gen.top";
	public static final String KEY_CROSS = "svm.opt.gen.cross";
	public static final String KEY_TEST_MODE = "svm.test.mode";
	public static final String KEY_TEST_RESULT = "svm.test.result";
	public static final String KEY_DATA_ATTR = "svm.data.attr";
	public static final String KEY_RUN_MODE = "svm.run.mode";
	public static final String KEY_LIST_NOUN = "svm.list.noun";
	public static final String KEY_DICT_NOUN = "svm.dict.noun";
	public static final String CLASSFIER_PROPERTIES_INTRO = 
		"\nClassifier Options" + 
		"\nRun mode: 0 = Dump Data, 1 = Optimize, 2 = Train and Predict, 3 = Optimize, Train and Predict" +
		"\n    svm.run.mode=0/1/2/3" +
		"\n\nSVM parameters: default; lowest; highest; change_step" +
		"\n    svm.gamma=d;l;h;step" +
		"\n    svm.wreq=d;l;h;step  (Weight of Require)" +
		"\n    svm.wexc=d;l;h;step  (Weight of Exclude)" +
		"\n\nCross Validation Fold" +
		"\n    svm.cv=int" +
		"\n\nOptimization Pass" +
		"\n    svm.opt.pass=int" +
		"\n\nGenetic Algorithm parameters" +
		"\n    svm.opt.gen.popsize=int  (Size of population)" +
		"\n    svm.opt.gen.iter=int  (Number of generations)" +
		"\n    svm.opt.gen.top=0 to 1  (Proportion of top elites)" +
		"\n    svm.opt.gen.cross=0 to 1  (Probability of Crossover)" +
		"\n\nPrediction options" +
		"\n    svm.test.mode=0/1  (Mode: 0 = ALL, 1 = BLANK)" +
		"\n    svm.test.result=int  (Number of displayed results)" +
		"\n\nData sets" +
		"\n    svm.train.fm=Name1;Name2;...;Name_N  (Name of training FMs)" +
		"\n    svm.test.fm=Name1;Name2;...;Name_N  (Name of test FMs)" +
		"\n    svm.data.attr=0;1;2;...;N   (Index of attributes, see below)" +
		"\n        " + SVMDataFormatter.attrInfo() +
		"\n\nData preprocessing" +
		"\n    svm.list.noun=FilePath    (Output all nouns in constrained-pairs)" +
		"\n    svm.dict.noun=FilePath    (Dictionary of noun keywords)";
	
	private void updateDataAttrInfo() {
		String[] attrs = cfg.getProperty(KEY_DATA_ATTR).split(";");
		SVMDataFormatter.updateAttrList(attrs);
		this.numDataAttr = attrs.length;
	}
	
	// Run mode #0
	public void dumpData() {
		updateDataAttrInfo();
		List<Model> trainFMs = getFMs(cfg, KEY_TRAIN_FM);
		List<Model> testFMs = getFMs(cfg, KEY_TEST_FM);
		
		this.dumpTrainingSet(trainFMs, testFMs);
	}
	
	// Run mode #1
	public void optimizeParameters() {
		updateDataAttrInfo();
		this.cvResult = new SVM.CV();
		
		List<Model> trainFMs = getFMs(cfg, KEY_TRAIN_FM);
		List<Model> testFMs = getFMs(cfg, KEY_TEST_FM);
		
		this.dumpTrainingSet(trainFMs, testFMs);
		
		String[] gammas = cfg.getProperty(KEY_GAMMA).split(";");
		String[] wreqs = cfg.getProperty(KEY_WREQ).split(";");
		String[] wexcs = cfg.getProperty(KEY_WEXC).split(";");
		
		//this.gammaLo = Double.valueOf(gammas[1]);
		//this.gammaHi = Double.valueOf(gammas[2]);
		double defaultGamma = 1.0 / this.numDataAttr;
		this.gammaLo = defaultGamma / 2;
		this.gammaHi = defaultGamma * 2;
		this.gammaStep = Double.valueOf(gammas[3]);
		this.reqLo = Integer.valueOf(wreqs[1]);
		this.reqHi = Integer.valueOf(wreqs[2]);
		this.reqStep = Integer.valueOf(wreqs[3]);
		this.excLo = Integer.valueOf(wexcs[1]);
		this.excHi = Integer.valueOf(wexcs[2]);
		this.excStep = Integer.valueOf(wexcs[3]);
		this.cvFold = Integer.valueOf(cfg.getProperty(KEY_CV));
		
		int pass = Integer.valueOf(cfg.getProperty(KEY_OPT_PASS));
		
		GeneticOptimizer o = new GeneticOptimizer();
		o.population = Integer.valueOf(cfg.getProperty(KEY_POPSIZE));
		o.generation = Integer.valueOf(cfg.getProperty(KEY_ITER));
		o.breedProb = Double.valueOf(cfg.getProperty(KEY_CROSS));
		o.elite = Float.valueOf(cfg.getProperty(KEY_TOP));
		
		Solution best = null;
		for (int i = 0; i < pass; i++) {
			long startTime = System.currentTimeMillis();
			logger.info("[opt] *** Optimizing Parameters (Pass " + (i+1) + " of " + pass + ')');
			Solution localBest = o.optimize(this);
			if (best == null || best.cost > localBest.cost) {
				best = localBest;
			}
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("[opt] *** Optimizing over, time elapsed: "
					+ (elapsedTime / 1000.0f) + " seconds.");
			logger.debug("[opt] *** Local Optimized Parameter:" + "\n\tgamma = "
					+ localBest.parts[0].value + "\n\tweight of require class = "
					+ localBest.parts[1].value + "\n\tweight of exclude class = "
					+ localBest.parts[2].value + "\nAccuracy = " + (100 - localBest.cost)
					+ "%");
		}
		
		logger.info("[opt] *** Optimized Parameter:" + "\n\tgamma = "
				+ best.parts[0].value + "\n\tweight of require class = "
				+ best.parts[1].value + "\n\tweight of exclude class = "
				+ best.parts[2].value + "\nAccuracy = " + (100 - best.cost)
				+ "%");
		
		cfg.setProperty(KEY_GAMMA, best.parts[0].value + ";"
				+ this.gammaLo + ";" + this.gammaHi + ";" + this.gammaStep);
		cfg.setProperty(KEY_WREQ, best.parts[1].value + ";"
				+ this.reqLo + ";" + this.reqHi + ";" + this.reqStep);
		cfg.setProperty(KEY_WEXC, best.parts[2].value + ";"
				+ this.excLo + ";" + this.excHi + ";" + this.excStep);
		try {
			cfg.store(new FileWriter("src/main/config/" + CFG_FILE), 
					"Updated at " + (new Date().toString()) +
					"\n" + CLASSFIER_PROPERTIES_INTRO);
		} catch (IOException e) {
			logger.warn("Cannot save property file.");
		}
	}
	
	// Run mode #2: train and predict, no optimization (use the parameters
	// defined in the properties file.)
	public void trainAndPredict() {
		updateDataAttrInfo();
		String[] gammas = cfg.getProperty(KEY_GAMMA).split(";");
		String[] wreqs = cfg.getProperty(KEY_WREQ).split(";");
		String[] wexcs = cfg.getProperty(KEY_WEXC).split(";");
		this.gamma = Double.valueOf(gammas[0]);
		this.reqWeight = Double.valueOf(wreqs[0]).intValue();
		this.excWeight = Double.valueOf(wexcs[0]).intValue();
		
		int numDisplayResult = Integer.valueOf(cfg.getProperty(KEY_TEST_RESULT));
		int testMode = Integer.valueOf(cfg.getProperty(KEY_TEST_MODE));
		int again = 0;
		do {
			List<Model> trainFMs = getFMs(cfg, KEY_TRAIN_FM);
			List<Model> testFMs = getFMs(cfg, KEY_TEST_FM);
			
			this.dumpTrainingSet(trainFMs, testFMs);
			this.trainWithoutCV();
			
			List<FeaturePair> testPairs = this.dumpTestSet(testFMs, testMode);
			
			this.predict();
			
			int pairIndex = 0;
			
			try {
				logger.info("\nPrediction checking begin.");
				// Open the prediction result file and display results.
				BufferedReader result = new BufferedReader(new FileReader(SVM.PREDICT_RESULT_FILE));
					
				String s;
				int numCorrect = 0, numPrediction = 0;
				while (numPrediction < numDisplayResult && (s = result.readLine()) != null) {
					int label = Float.valueOf(s).intValue();
					FeaturePair cur = testPairs.get(pairIndex++);
					if (label != cur.getLabel()) {
						// If this label is different from the original one, it is 
						// a prediction needed checking.
						numPrediction++;
						// Print the prediction to user
						System.out.print("\n" + cur.getPairInfo());
						System.out.println("\n-->Prediction is: " +
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
				
			} catch (FileNotFoundException e) {
				logger.warn("Cannot open prediction file.", e);
			} catch (IOException e) {
				logger.warn("Cannot read prediction file.", e);
			}
			
			System.out.print("\nPrediction end. Do it again? (0 = NO, 1 = YES):");
			Scanner scanner = new Scanner(System.in);
			again = scanner.nextInt();
		} while (again == 1);
		
	}
	
	// Run mode #3 (Do not support now)
	
	public static final int RUN_DUMP_DATA = 0; 
	public static final int RUN_OPT = 1;
	public static final int RUN_PREDICT = 2;
	public static final int RUN_OPT_AND_PREDICT = 3;
	public static final String CFG_FILE = "classifier.properties";
	
	// The main method checks the run mode
	public static void main(String[] argv) throws IOException {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		SVM svm = null;
		try {
			session.beginTransaction();
			
			Properties cfg = new Properties();
			URL url = SVM.class.getClassLoader().getResource(CFG_FILE);
			cfg.load(url.openStream());
			
			int runMode = Integer.valueOf(cfg.getProperty(KEY_RUN_MODE));
			svm = new SVM(cfg);
			svm.prepareFiles();
			switch (runMode) {
			case RUN_DUMP_DATA:
				svm.dumpData();
				break;
			case RUN_OPT:
				svm.optimizeParameters();
				break;
			case RUN_PREDICT:
				svm.trainAndPredict();
				break;
			case RUN_OPT_AND_PREDICT:
				//TODO: add run mode support. 
				break;
			}
		
			session.getTransaction().commit();
			
		} catch (HibernateException he) {
			session.getTransaction().rollback();
			logger.error("Database error.", he);
			session.close();
		} finally {
			if (svm != null) {
				svm.dispose();
			}
		}
	}

	public void prepareFiles() {
		String dictNounFile = cfg.getProperty(KEY_DICT_NOUN);
		try {
			this.nounDictFile = new BufferedReader(new FileReader(dictNounFile));
		} catch (FileNotFoundException e) {
			logger.info("No noun-keyword file assigned.");
			nounDictFile = null;
		}
	}

	public void addFilter(PairFilter pf) {
		filters.add(pf);
	}
	
	public void dispose() {
		for (PairFilter filter: filters) {
			filter.dispose();
		}
	}
	
	private static class PairStats {
		
		private int[] simTotal = new int[33];
		private int[] simVerb = new int[33];
		private int[] simNoun = new int[33];
		
		private int[] reqOutNo = {0, 0, 0}; // require outsider = NO
		private int[] reqOutYes = {0, 0, 0}; // require outsider = 1 or 2
		
		private int[] excOutNo = {0, 0, 0}; // exclude outsider = NO
		private int[] excOutYes = {0, 0, 0}; // exclude outsider = 1 or 2
		
		public String toString() {
			String head = String.format("*** Attribute distribution over class.\n"
					+ "%10s%10s%10s%10s\n"
					+ "-------------------------------------------------------\n",
					" ", "NO_CONS", "REQUIRE", "EXCLUDE");
			String simAttrs = formatSimInfo("Sim_Total", simTotal)
					+ formatSimInfo("Sim_Verb", simVerb)
					+ formatSimInfo("Sim_Noun", simNoun);
			String otherAttrs = String.format(
					"%10s%10d%10d%10d\n"   // No require out
					+ "%10s%10d%10d%10d\n"   // Has 
					+ "%10s%10d%10d%10d\n"   // No exclude out
					+ "%10s%10d%10d%10d",    // Has
					"No_Req_Out", reqOutNo[0], reqOutNo[1], reqOutNo[2],
					"Has", reqOutYes[0], reqOutYes[1], reqOutYes[2],
					"No_Exc_Out", excOutNo[0], excOutNo[1], excOutNo[2],
					"Has", excOutYes[0], excOutYes[1], excOutYes[2]
					);
			return head + simAttrs + otherAttrs;
		}
		
		private String formatSimInfo(String title, int[] sim) {
			String s = title + "\n";
			for (int i = 0; i < 11; i++) {
				s += String.format("%10s%10d%10d%10d\n", 
						(i ==10 ? "1.0" : "[0." + i + "~"), 
						sim[i*3], sim[i*3+1], sim[i*3+2]);
			}
			return s;
		}
		
		// show stats about attributes and class correspondence
		public void addPair(List<FeaturePair> pairs) {
			for (FeaturePair pair: pairs) {
				addSimInfo(pair.getTotalSim(), pair.getLabel(), this.simTotal);
				addSimInfo(pair.getVerbSim(), pair.getLabel(), this.simVerb);
				addSimInfo(pair.getNounSim(), pair.getLabel(), this.simNoun);
				
				int index = (pair.getLabel() == FeaturePair.NO_CONSTRAINT ? 0 : 
					(pair.getLabel() == FeaturePair.REQUIRE ? 1 : 2));
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
		
		private void addSimInfo(double sim, int label, int[] a) {
			int offset = (label == FeaturePair.NO_CONSTRAINT ? 0 :
				(label == FeaturePair.REQUIRE ? 1 : 2));
			int base = Double.valueOf(Math.floor(10 * sim)).intValue();
			a[base + offset]++;
		}
		
	}
	
	// result for cross-validation (CV)
	public static class CV {
		public double accuracy;
		public double meanSquareError;
		public double squareCoefficient;
	}
}
