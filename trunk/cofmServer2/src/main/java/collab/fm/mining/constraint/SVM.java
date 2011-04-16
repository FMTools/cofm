package collab.fm.mining.constraint;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.util.exception.SvmException;

public class SVM implements Optimizable {

	static Logger logger = Logger.getLogger(SVM.class);
	
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
	
	public SVM.CV cvResult;
	
	// Train and then do cross-validation (CV)
	public boolean trainWithCV() {
		this.scaleData(SVM.TRAINING_FILE + SVM.SCALED_FILE_SUFFIX,
				SVM.ARG_SCALE_TRAINING);
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
	
	public boolean train() {
		this.scaleData(SVM.TRAINING_FILE + SVM.SCALED_FILE_SUFFIX,
				SVM.ARG_SCALE_TRAINING);
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
	
	public boolean predict(String outputFile) {
		if (this.scaleData(TEST_FILE + SCALED_FILE_SUFFIX, ARG_SCALE_TEST)) {
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
		return false;
	}
	
	/**
	 * Generate data file for LIBSVM. 
	 * @param model The feature model to be analyzed.
	 * @throws IOException 
	 */
	public boolean dumpDataFile(Model model, String outputFile) {
		logger.info("*** Dump Data File");
		BufferedWriter tf;
		try {
			tf = new BufferedWriter(new FileWriter(outputFile));
		
			Entity[] features = model.getEntities().toArray(new Entity[0]);
			int numPair = 0, numRequire = 0, numExclude = 0;
			List<Float> simPairs = new ArrayList<Float>(); 
			for (int i = 0; i < features.length; i++) {
				for (int j = i+1; j < features.length; j++) {
					FeaturePair pair = new FeaturePair(features[i], features[j]);
					tf.write(formatPair(pair) + "\n");
					if (pair.getSimilarity() > 0.0f) {
						simPairs.add(pair.getSimilarity());
					}		
					if (pair.getLabel() == FeaturePair.REQUIRE) {
						numRequire++;
					} else if (pair.getLabel() == FeaturePair.EXCLUDE) {
						numExclude++;
					}
					numPair++;
				}
			}
			tf.close();
			logger.info("*** Dump Data File END.");
			logger.info(features.length + " features, " + numPair + " pairs, " 
					+ numRequire + " require-pairs, "
					+ numExclude + " exclude-pairs, "
					+ simPairs.size() + " pairs of similar feaures.");
			Float[] sims = simPairs.toArray(new Float[0]);
			Arrays.sort(sims);
			String s = "Sorted similarity:\n";
			for (int i = 0; i < sims.length;) {
				int j = i;
				for (; j < sims.length && sims[i].equals(sims[j]); j++) {
					// empty loop body
				}
				s += "\t" + sims[i].toString();
				if (j - i > 1) {
					s += "  (" + (j-i) + ")";
				}
				s += "\n";
				i = j;
			}
			logger.debug(s);
			return true;
		} catch (IOException e) {
			logger.error("Cannot open data file", e);
			return false;
		}
	}
	
	public boolean scaleData(String outputFile, String parameters) {
		logger.info("*** Scale data to [-1, 1]");
		PrintStream stdout = System.out;
		try {
			// First, we need to redirect the System.out to a file
			PrintStream scaleFile = new PrintStream(outputFile);
			System.setOut(scaleFile);
			
			// Call the svm_scale.run() with arguments
			svm_scale s = new svm_scale();
			s.run(parameters.split("\\s"));
			
			scaleFile.close();
			logger.info("*** Scale END.");
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
	
	private String formatPair(FeaturePair pair) {
		return pair.getLabel() + 
			   " 1:" + pair.getSimilarity() +
			   " 2:" + pair.getParental() +
			   " 3:" + pair.getSibling()  + 
			   " 4:" + pair.getNumMandatory() + 
			   " 5:" + pair.getRequireOut() +
			   " 6:" + pair.getExcludeOut() +
			   " 7:" + pair.getParentRequireOut() +
			   " 8:" + pair.getParentExcludeOut();
	}
	
	// result for cross-validation (CV)
	public static class CV {
		public double accuracy;
		public double meanSquareError;
		public double squareCoefficient;
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
			new Domain(false, SVM.DEFAULT_GAMMA / 2, SVM.DEFAULT_GAMMA * 2, 0.02),
			new Domain(true, 10, 70, 1),
			new Domain(true, 10, 70, 1) 
		};
		Solution s = new Solution();
		s.parts = parts;
		return s;
	}
	
	public Solution optimizeParameters(Model model) {
		this.cvResult = new SVM.CV();
		if (this.dumpDataFile(model, SVM.TRAINING_FILE)) {
			
			logger.info("*** Optimizing Parameters");
			long startTime = System.currentTimeMillis();
			Optimizer o = new GeneticOptimizer();
			Solution best = o.optimize(this);
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("*** Optimizing over, time elapsed: " + (elapsedTime / 1000.0f) + " seconds.");
			logger.info("*** Optimized Parameter:" +
					"\n\tgamma = " + best.parts[0].value +
					"\n\tweight of require class = " + best.parts[1].value +
					"\n\tweight of exclude class = " + best.parts[2].value +
					"\nAccuracy = " + (100 - best.cost) + "%");
			return best;
			
		}
		return null;
	}
	
	public void predictAndCheck(Model training, Model test) {
		if (this.dumpDataFile(training, SVM.TRAINING_FILE)
				&& this.dumpDataFile(test, SVM.TEST_FILE)) {
			this.train();
			this.predict(SVM.PREDICT_RESULT_FILE);
			// TODO: open the result and show all constraints on the screen,
			// and ask users to confirm the results.
		}
	}
	
	// Find optimized parameters
	public static void main(String[] argv) throws IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();;
		try {
			session.beginTransaction();
			
			Model model = DaoUtil.getModelDao().getByName("Media Player");
			if (model == null) {
				logger.warn("No such model");
			} else {
				//new SVM().optimizeParameters(model);
				new SVM().predictAndCheck(model, model);
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
	
}
