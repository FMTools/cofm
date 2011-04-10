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

import collab.fm.mining.opt.Optimizable;
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
	
	// ----------- Parameters ------------
	// We can adjust these parameters to find optimized values.
	
	// Scale the data to [-1, 1]
	public static final String ARG_SCALE = 
		"-l -1 -u 1 -s " + SCALE_RANGE_FILE + " " + TRAINING_FILE;
	
	// Training: Default gamma = 1 / number of attributes
	public static boolean useDefaultGamma = true;
	public static final double DEFAULT_GAMMA = (double) 1 / FeaturePair.NUM_ATTRIBUTES;
	public static double gamma = DEFAULT_GAMMA;
	public static int reqWeight = 5;   // Bonus for finding a "require" constraint.
	public static int excWeight = 5;   // Bonus for finding a "exclude" constraint.
	public static int cvFold = 4;    // The fold of CV
	
	public static String ARG_TRAIN = 
		(useDefaultGamma ? "" : "-g " + gamma + " ")
		+ "-w1 " + reqWeight + " -w2 " + excWeight + " -v " + cvFold + " " + TRAINING_FILE;
	
	public SVM.CV cvResult;
	
	// Train and then do cross-validation (CV)
	public boolean trainWithCV() {
		svm_train t = new svm_train();
		try {
			t.run(SVM.ARG_TRAIN.split("\\s"), cvResult);
			return true;
		} catch (IOException e) {
			logger.error("IO error.", e);
			return false;
		} catch (SvmException e) {
			logger.error("Training error.", e);
			return false;
		}
	}
	
	/**
	 * Generate training file for LIBSVM. 
	 * @param model The feature model to be analyzed.
	 * @throws IOException 
	 */
	public boolean dumpTrainingFile(Model model) {
		logger.info("*** Dump Training File");
		BufferedWriter tf;
		try {
			tf = new BufferedWriter(new FileWriter(SVM.TRAINING_FILE));
		
			Entity[] features = model.getEntities().toArray(new Entity[0]);
			int numPair = 0;
			List<Float> simPairs = new ArrayList<Float>(); 
			for (int i = 0; i < features.length; i++) {
				for (int j = i+1; j < features.length; j++) {
					FeaturePair pair = new FeaturePair(features[i], features[j]);
					tf.write(formatPair(pair) + "\n");
					if (pair.getSimilarity() > 0.0f) {
						simPairs.add(pair.getSimilarity());
					}		
					numPair++;
				}
			}
			tf.close();
			logger.info("*** Dump Training File END.");
			logger.info(features.length + " features, " + numPair + " pairs, " + simPairs.size() + " pairs of similar feaures.");
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
			logger.error("Cannot open training file", e);
			return false;
		}
	}
	
	public boolean scaleData() {
		logger.info("*** Scale data to [-1, 1]");
		PrintStream stdout = System.out;
		try {
			// First, we need to redirect the System.out to a file
			PrintStream scaleFile = new PrintStream(SVM.TRAINING_FILE + SVM.SCALED_FILE_SUFFIX);
			System.setOut(scaleFile);
			
			// Call the svm_scale.run() with arguments
			svm_scale s = new svm_scale();
			s.run(SVM.ARG_SCALE.split("\\s"));
			
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
			   " 6:" + pair.getExcludeOut();
	}
	
	public static void main(String[] argv) throws IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();;
		try {
			session.beginTransaction();
			
			Model model = DaoUtil.getModelDao().getByName("Media Player");
			if (model == null) {
				logger.warn("No such model");
			} else {
				SVM svm = new SVM();
				svm.cvResult = new SVM.CV();
				if (svm.dumpTrainingFile(model)) {
					svm.scaleData();
					svm.trainWithCV();
					logger.info("Cross Validation Accuracy = " + svm.cvResult.accuracy + "%");
				}
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
	
	// result for cross-validation (CV)
	public static class CV {
		public double accuracy;
		public double meanSquareError;
		public double squareCoefficient;
	}

	// Cost = 1 - Accuracy
	public double computeCost(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}

	// Solution = [gamma, reqWeight, excWeight]
	public Solution defineSolution() {
		// TODO Auto-generated method stub
		return null;
	}
}
