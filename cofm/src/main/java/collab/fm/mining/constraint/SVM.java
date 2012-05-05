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

import collab.fm.mining.constraint.Prediction.Metric;
import collab.fm.mining.constraint.filter.*;
import collab.fm.mining.constraint.stats.*;
import collab.fm.mining.opt.Domain;
import collab.fm.mining.opt.GeneticOptimizer;
import collab.fm.mining.opt.Optimizable;
import collab.fm.mining.opt.Optimizer;
import collab.fm.mining.opt.Solution;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.persist.relation.Relation;
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
	private List<DataStats> stats = new ArrayList<DataStats>();
	
	public static final int MAX_PREDICTION_NUM = 20;
	public static final String TRAINING_FILE = "log/cofm/mining/cons_svm_train";
	public static final String SCALED_FILE_SUFFIX = ".scale";
	public static final String SCALE_RANGE_FILE = "log/cofm/mining/cons_svm_scale_range";
	public static final String MODEL_FILE = "log/cofm/mining/cons_svm_model";
	public static final String TEST_FILE = "log/cofm/mining/cons_svm_test";
	public static final String PREDICT_RESULT_FILE = "log/cofm/mining/cons_svm_predict";
	
	// ----------- Arguments for calling LIBSVM ------------
	// Scale the data to [-1, 1]
	public static final String ARG_SCALE_TRAINING = 
		"-l -1 -u 1 -s " + SCALE_RANGE_FILE + " " + TRAINING_FILE;
	
	public static final String ARG_SCALE_TEST = 
		"-r " + SCALE_RANGE_FILE + " " + TEST_FILE;
	
	// Parameters for SVM algorithm
	private int numDataAttr;
	private double defaultGamma, gamma, gammaLo, gammaHi, gammaStep;
	private int reqWeight, reqLo, reqHi, reqStep;
	private int excWeight, excLo, excHi, excStep;
	private int cvFold;
	
	private int trainSource;
	private int feedback;
	private int testPass;
	private static final int FROM_TRAIN_MODEL = 0;
	private static final int FROM_TRAIN_AND_TEST_MODEL = 1;
	private static final int FROM_TEST_MODEL = 2;
	private static final int FROM_TEST_MODEL_ITERATED = 3;
	
	private double minIterateDiff;
	
	private List<FeaturePair> trainPool = new ArrayList<FeaturePair>();
	private List<FeaturePair> trainData, testData;
	private List<FeaturePair> testPool = new ArrayList<FeaturePair>();
	private int currentTestPass;
	
	// Store the cross-validation result.
	public SVM.CV cvResult;
	
	private BufferedReader nounDictFile;
	
	private Properties cfg;
	
	public SVM(Properties cfg) {
		this.cfg = cfg;
		buildFilterChain();
		buildStatsChain();
	}
	
	private void buildStatsChain() {
		stats.clear();
		stats.add(new LocalAttributeStats());
	}
	
	private void buildFilterChain() {
		filters.clear();
		//filters.add(new SimilarityFilter(0.0));
		filters.add(new CrossTreeOnlyFilter());
	}
	
	// ------------ Step 1. Output data -------------
	private boolean keepPair(FeaturePair pair) {
		for (int i = 0; i < filters.size(); i++) {
			if (!filters.get(i).keepPair(pair)) {
				return false;
			}
		}
		return true;
	}
	
	private void updateStats(List<FeaturePair> pairs) {
		for (DataStats ds: stats) {
			ds.update(pairs);
		}
	}
	
	private void showStats() {
		String file = cfg.getProperty(KEY_STATS_FILE);
		if (file != null) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				for (DataStats ds: stats) {
					ds.report(out);
					out.write("\n");
				}
				out.close();
			} catch (IOException e) {
				logger.warn("Cannot write stats.", e);
			}
		} else {
			logger.info("Stats file is unassigned.");
		}
	}
	
	
	public static final int MODE_TEST_ALL = 0;
	public static final int MODE_TEST_BLANK = 1;
	public static final int MODE_TRAIN_ALL = 2; 
	public static final int MODE_TRAIN_ONLY_CON = 3;
	public static final String[] modeName = {
		"Test_All", "Test_Blank", "Train_All", "Train_Only_Con"
	};
	
	private void refreshDataFiles() {
		trainData = new ArrayList<FeaturePair>();
		testData = new ArrayList<FeaturePair>();
		
		if (this.trainSource == FROM_TRAIN_MODEL) {
			trainData = trainPool;
			testData = testPool;
		} else if (this.trainSource == FROM_TRAIN_AND_TEST_MODEL) {
			trainData.addAll(trainPool);
			trainData.addAll(getCurrentPartOfTestPool());
			testData.addAll(getRestPartOfTestPool());
		} else {
			trainData.addAll(getCurrentPartOfTestPool());
			testData.addAll(getRestPartOfTestPool());
		}
		
		
		writeDataToFile(trainData, testData);
		
	}
	
	private List<FeaturePair> getCurrentPartOfTestPool() {
		int subSize = testPool.size() / this.testPass;
		int begin = this.currentTestPass * subSize;
		int end = begin + subSize;
		if (end > testPool.size()) end = testPool.size();
		return testPool.subList(begin, end);
	}
	
	private List<FeaturePair> getRestPartOfTestPool() {
		int subSize = testPool.size() / this.testPass;
		int begin = this.currentTestPass * subSize;
		int end = begin + subSize;
		if (end > testPool.size()) end = testPool.size();
		
		if (end - begin >= testPool.size()) {
			return new ArrayList<FeaturePair>();  // empty
		}
		
		if (begin == 0) {
			return testPool.subList(end, testPool.size());
		}
		
		if (end == testPool.size()) {
			return testPool.subList(0, begin);
		}
		
		List<FeaturePair> result = testPool.subList(0, begin);
		result.addAll(testPool.subList(end, testPool.size()));
		return result;
	}
	
	private void writeDataToFile(List<FeaturePair> train, List<FeaturePair> test) {
		logger.info("Training Set: " + train.size() + " pairs; Test Set:: " + test.size() + " pairs.");
		
		writeToFile(train, SVM.TRAINING_FILE);
		this.scaleTrainingSet();
		
		writeToFile(test, SVM.TEST_FILE);
		this.scaleTestSet();
		
		this.showStats();
	}
	
	private void dumpModelsIntoPool(List<Model> train, List<Model> test) {
		trainPool.clear();
		testPool.clear();
		
		for (Model m: train) {
			dumpToPool(m, trainPool);
		}
		
		for (Model m: test) {
			dumpToPool(m, testPool);
		}
		
		this.updateStats(trainPool);
		
		// Shuffle test pool in specific modes
		if (this.trainSource != FROM_TRAIN_MODEL) {
			Collections.shuffle(this.testPool);
		}
	}
	
	private void writeToFile(List<FeaturePair> list, String file) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (FeaturePair p: list) {
				out.write(SVMDataFormatter.format(p.getLabel(), p) + "\n");
			}
			out.close();
		} catch (IOException e) {
			logger.warn("Cannot write pair.", e);
		}
	}
	
	private void dumpToPool(Model model, List<FeaturePair> pool) {
		FeaturePair.clearFeatureSet();
		Entity[] features = model.getEntities().toArray(new Entity[0]);
		// Random shuffle the features.
		Collections.shuffle(Arrays.asList(features));

		List<FeaturePair> pairs = new ArrayList<FeaturePair>();
		
		for (int i = 0; i < features.length; i++) {
			for (int j = i + 1; j < features.length; j++) {
				pairs.add(new FeaturePair(features[i], features[j]));
			}
		}

		int total = 0, numSim = 0, numRequire = 0, numExclude = 0;
		// Complete the pair similarity calculation and output them.
		for (FeaturePair p: pairs) {
			p.updateTextSimilarity();
			if (this.keepPair(p)) {
				total++;
				pool.add(p);
				if (p.getLabel() == FeaturePair.REQUIRE) {
					numRequire++;
				} else if (p.getLabel() == FeaturePair.EXCLUDE) {
					numExclude++;
				}
				if (p.getTotalSim() > 0.0) {
					numSim++;
				}
			}
		}
		logger.info("Feature Model: '" + model.getName() + "': " 
				+ features.length + " features, " 
				+ total + " valid pairs, including "
				+ numRequire + " require-pairs, " + numExclude
				+ " exclude-pairs, and " + numSim + " pairs of similar features.");
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
				+ " -w-1 1"
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
				+ " -w-1 1"
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
			new Domain(false, gammaLo, gammaHi, gammaStep, defaultGamma),
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
		req.setRefine(false);
		int predicate = (type == EXCLUDE ? Relation.EXCLUDE : Relation.REQUIRE);
		if (type == REQUIRED_BY) {
			req.setSignature(predicate + "(" + pair.getSecond().getId() + "," + pair.getFirst().getId() + ')');
		} else {
			req.setSignature(predicate + "(" + pair.getFirst().getId() + "," + pair.getSecond().getId() + ')');
		}
		req.setYes(true);
		try {
			req.setRequesterId(DaoUtil.getUserDao().getByName("admin").getId());
		
			req.process(new ResponseGroup());
			
			if (type == MUTUAL_REQUIRE) {
				req.setSignature(predicate + "(" + pair.getSecond().getId() + "," + pair.getFirst().getId() + ')');
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
	private static final int DO_UPMERGE = 1;
	private static final int DO_HUMAN_FEEDBACK = 1;
	private static final int DO_SIMULATED_FEEDBACK = 2;
	
	public static final String KEY_TRAIN_FM = "svm.train.fm";
	public static final String KEY_TEST_FM = "svm.test.fm";
	public static final String KEY_TRAIN_SOURCE = "svm.train.source";
	private static final String KEY_MIN_ITERATE_DIFF = "svm.train.iterate.diff";
	public static final String KEY_TEST_PASS = "svm.test.pass";
	public static final String KEY_TEST_UPMERGE = "svm.test.upmerge";
	public static final String KEY_GAMMA = "svm.gamma";
	public static final String KEY_WREQ = "svm.wreq";
	public static final String KEY_WEXC = "svm.wexc";
	public static final String KEY_CV = "svm.cv";
	public static final String KEY_OPT_PASS = "svm.opt.pass";
	public static final String KEY_POPSIZE = "svm.opt.gen.popsize";
	public static final String KEY_ITER = "svm.opt.gen.iter";
	public static final String KEY_TOP = "svm.opt.gen.top";
	public static final String KEY_CROSS = "svm.opt.gen.cross";
	public static final String KEY_TEST_RESULT = "svm.test.result";
	public static final String KEY_TEST_FEEDBACK = "svm.test.feedback";
	public static final String KEY_DATA_ATTR = "svm.data.attr";
	public static final String KEY_RUN_MODE = "svm.run.mode";
	public static final String KEY_STATS_FILE = "svm.stats.file";
	public static final String KEY_DICT_NOUN = "svm.dict.noun";
	public static final String CLASSFIER_PROPERTIES_INTRO = 
		"\nClassifier Options" + 
		"\nRun mode: 0 = Dump Data, 1 = Optimize, 2 = Train and Test" +
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
		"\n\nTest options" +
		"\n    svm.test.result=int  (Number of feedback results)" +
		"\n    svm.test.pass=int  (Test how many passes)" +
		"\n    svm.test.upmerge=0/1 (Upmerge the constraints?)" +
		"\n    svm.test.feedback=0/1/2 (1 = Human feedback; 2 = Sim feedback)" +
		"\n\nData sets" +
		"\n    svm.train.fm=Name1;Name2;...;Name_N  (Name of training FMs)" +
		"\n    svm.test.fm=Name1;Name2;...;Name_N  (Name of test FMs)" +
		"\n    svm.train.source=0/1/2/3 (0 = Use Train FM, 1 = Use Train FM and part of Test FM, " +
		"2 = Use part of Test FM, 3 = Iterated use part of Test FM (EM Algorithm))" +
		"\n    svm.train.iterate.diff=double (0 to 1, the iterate end condition)" +
		"\n    svm.data.attr=0;1;2;...;N   (Index of attributes, see below)" +
		"\n        " + SVMDataFormatter.attrInfo() +
		"\n    svm.stats.file=FileName    (The file for data stats report)" +
		"\n\nData preprocessing" +
		"\n    svm.dict.noun=FilePath    (Dictionary of noun keywords)";
	
	private void updateGeneralInfo() {
		// Data attributes info
		String[] attrs = cfg.getProperty(KEY_DATA_ATTR).split(";");
		SVMDataFormatter.updateAttrList(attrs);
		this.numDataAttr = attrs.length;
		
		// Train and test model info
		this.trainSource = Integer.valueOf(cfg.getProperty(KEY_TRAIN_SOURCE));
		this.testPass = Integer.valueOf(cfg.getProperty(KEY_TEST_PASS));
		
		if (this.trainSource == FROM_TRAIN_MODEL) {
			this.testPass = 1;  // force test pass to 1
		}
		
		this.feedback = Integer.valueOf(cfg.getProperty(KEY_TEST_FEEDBACK));
		this.minIterateDiff = Double.valueOf(cfg.getProperty(KEY_MIN_ITERATE_DIFF));
	}
	
	// Run mode #0
	public void dumpData() {
		updateGeneralInfo();
		List<Model> trainFMs = getFMs(cfg, KEY_TRAIN_FM);
		List<Model> testFMs = getFMs(cfg, KEY_TEST_FM);
		
		this.dumpModelsIntoPool(trainFMs, testFMs);
		this.refreshDataFiles();
	}
	
	// Run mode #1
	public void optimizeParameters() {
		updateGeneralInfo();
		this.cvResult = new SVM.CV();
		
		List<Model> trainFMs = getFMs(cfg, KEY_TRAIN_FM);
		List<Model> testFMs = getFMs(cfg, KEY_TEST_FM);
		
		this.dumpModelsIntoPool(trainFMs, testFMs);
		
		String[] gammas = cfg.getProperty(KEY_GAMMA).split(";");
		String[] wreqs = cfg.getProperty(KEY_WREQ).split(";");
		String[] wexcs = cfg.getProperty(KEY_WEXC).split(";");
		
		//this.gammaLo = Double.valueOf(gammas[1]);
		//this.gammaHi = Double.valueOf(gammas[2]);
		this.defaultGamma = 1.0 / this.numDataAttr;
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
		this.currentTestPass = 0;
		while (this.currentTestPass < this.testPass) {
			this.refreshDataFiles();
			
			for (int i = 0; i < pass; i++) {
				logger.info("[opt] *** Optimizing Parameters (Pass " + (i+1) + " of " + pass + ')');
				Solution localBest = o.optimize(this);
				if (best == null || best.cost > localBest.cost) {
					best = localBest;
				}
				logger.info("[opt] *** Local Optimized Parameter:" + "\n\tgamma = "
						+ localBest.parts[0].value + "\n\tweight of require class = "
						+ localBest.parts[1].value + "\n\tweight of exclude class = "
						+ localBest.parts[2].value + "\nAccuracy = " + (100 - localBest.cost)
						+ "%");
			}
			
			this.currentTestPass++;
		}
		logger.info("[opt] *** Global Optimized Parameter:" + "\n\tgamma = "
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
	
	// Run mode #2: Optimize, Train and test
	public void trainAndTest() {
		updateGeneralInfo();
		this.cvResult = new SVM.CV();
		
		List<Model> trainFMs = getFMs(cfg, KEY_TRAIN_FM);
		List<Model> testFMs = getFMs(cfg, KEY_TEST_FM);
		
		this.dumpModelsIntoPool(trainFMs, testFMs);
		
		String[] gammas = cfg.getProperty(KEY_GAMMA).split(";");
		String[] wreqs = cfg.getProperty(KEY_WREQ).split(";");
		String[] wexcs = cfg.getProperty(KEY_WEXC).split(";");
		
		//this.gammaLo = Double.valueOf(gammas[1]);
		//this.gammaHi = Double.valueOf(gammas[2]);
		this.defaultGamma = 1.0 / this.numDataAttr;
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
		int upmerge = Integer.valueOf(cfg.getProperty(KEY_TEST_UPMERGE));
		int numDisplayResult = Integer.valueOf(cfg.getProperty(KEY_TEST_RESULT));
		
		GeneticOptimizer o = new GeneticOptimizer();
		o.population = Integer.valueOf(cfg.getProperty(KEY_POPSIZE));
		o.generation = Integer.valueOf(cfg.getProperty(KEY_ITER));
		o.breedProb = Double.valueOf(cfg.getProperty(KEY_CROSS));
		o.elite = Float.valueOf(cfg.getProperty(KEY_TOP));
		
		Prediction prediction = new Prediction();
		this.currentTestPass = 0;
		while (this.currentTestPass < this.testPass) {
			// Update data files
			logger.info("*** Refresh Data Sets.");
			this.refreshDataFiles();
			
			int doNextFeedback = 0;
			
			do {
				boolean trainAgain = false;
				List<FeaturePair> lastPrediction = null;
				
				do {
					// Find best parameter
					Solution best = null;
					for (int i = 0; i < pass; i++) {
						logger.debug("[opt] *** Optimizing Parameters (Pass " + (i+1) + " of " + pass + ')');
						Solution localBest = o.optimize(this);
						if (best == null || best.cost > localBest.cost) {
							best = localBest;
						}
						logger.debug("[opt] *** Local Optimized Parameter:" + "\n\tgamma = "
								+ localBest.parts[0].value + "\n\tweight of require class = "
								+ localBest.parts[1].value + "\n\tweight of exclude class = "
								+ localBest.parts[2].value + "\nAccuracy = " + (100 - localBest.cost)
								+ "%");
					}
					logger.info("[opt] *** Global Optimized Parameter:" + "\n\tgamma = "
							+ best.parts[0].value + "\n\tweight of require class = "
							+ best.parts[1].value + "\n\tweight of exclude class = "
							+ best.parts[2].value + "\nAccuracy = " + (100 - best.cost)
							+ "%");
					
					// Use the best parameter
					this.gamma = best.parts[0].value;
					this.reqWeight = Double.valueOf(best.parts[1].value).intValue();
					this.excWeight = Double.valueOf(best.parts[2].value).intValue();
					
					// ...and then re-train the classifier
					logger.info("*** Re-train the classifier.");
					this.trainWithoutCV();
					
					// Then do the test
					logger.info("*** Predicting...");
					this.predict();
					
					int pairIndex = 0;
					
					// Read the test result
					BufferedReader result;
					try {
						result = new BufferedReader(new FileReader(SVM.PREDICT_RESULT_FILE));
					
						String s;
						while ((s = result.readLine()) != null) {
							int label = Float.valueOf(s).intValue();
							testData.get(pairIndex).setPredictedClass(label);
							pairIndex++;
						}
						result.close();
					} catch (IOException e) {
						logger.warn("Fail to read results.", e);
					}
					
					if (this.trainSource == FROM_TEST_MODEL_ITERATED) {
						if (lastPrediction == null || 
								Prediction.diff(lastPrediction, testData) > this.minIterateDiff) {
							trainAgain = true;
							
							// Clone the current prediction (the "testData")
							lastPrediction = new ArrayList<FeaturePair>(testData.size());
							for (FeaturePair p: testData) {
								lastPrediction.add(new FeaturePair(p));
							}
							
							// Updated Training Set = Real Training Set + Predicted Test Set 
							List<FeaturePair> newTrainData = new ArrayList<FeaturePair>();
							newTrainData.addAll(this.trainData);
							for (FeaturePair p: lastPrediction) {
								p.setLabel(p.getPredictedClass());   // Must setLabel for training
							}
							newTrainData.addAll(lastPrediction);
							
							this.writeDataToFile(newTrainData, testData);
							
							logger.info("*** Do Next Iterated Training.");
						} else {
							trainAgain = false;
						}
					}
					
				} while (trainAgain);
				
				if (upmerge == DO_UPMERGE) {
					logger.info("*** Upmerge the Constraints...");
					prediction.upmergeConstraints(testData);
				}
					
				// Calculate the metrics (precision, recall, accuracy)
				logger.info("*** Update Accuracy/Precision/Recall");
				//prediction.push(testData);
				prediction.set(testData);
				
				Metric m1 = prediction.getClassMetric(FeaturePair.REQUIRE);
				Metric m2 = prediction.getClassMetric(FeaturePair.EXCLUDE);
				logger.info("*** RESULT ***");
				logger.info("Avg accuracy = " + prediction.avgAccuracy() + 
						"\nREQUIRES: avg precision = " + m1.avgPrecision() + ", avg recall = " + m1.avgRecall() +
						"\nEXCLUDES: avg precision = " + m2.avgPrecision() + ", avg recall = " + m2.avgRecall());
				
				if (this.feedback == DO_HUMAN_FEEDBACK || this.feedback == DO_SIMULATED_FEEDBACK) {
					logger.info("*** Feedback...");
					
					// 1. Choose feedback pairs
					List<FeaturePair> show = Prediction.selectFeedback(testData, numDisplayResult);
					
					int numCorrect = 0;
					// 2. Do real (on an unknown model) or simulated (on a known model) feedback
					if (this.feedback == DO_SIMULATED_FEEDBACK) {
						// In fact, there's nothing to do because the test data is already classified.
						for (FeaturePair cur: show) {
							if (cur.getLabel() == cur.getPredictedClass()) {
								numCorrect++;
							}
						}
					} else {
						// Ask human to check and classify the shown pairs.
						for (FeaturePair cur: show) {
							// Print the prediction to user
							System.out.print("\n" + cur.getPairInfo());
							System.out.println("\n-->Prediction is: " +
									(cur.getPredictedClass() == FeaturePair.EXCLUDE ? "EXCLUDE" : (
											cur.getPredictedClass() == FeaturePair.REQUIRE ? "REQUIRE"
													: "NO_CONSTRAINT")));
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
							
							if (type == cur.getPredictedClass()) {
								numCorrect++;
							}
							
							// if the answer type is different from the original pair, 
							// we need to apply the answer to the FM (i.e. add the constraint actually)
							if (type != cur.getLabel()) {
								if (type == FeaturePair.EXCLUDE || type == FeaturePair.REQUIRE) {
									persistConstraint(cur, answer);
									System.out.println("The constraint has been added to the test FM.");
								} else if (type == FeaturePair.NO_CONSTRAINT) {
									removeConstraint(cur);
									System.out.println("The constraint has been removed from the test FM.");
								}
							}
							
							cur.setLabel(type);  // setLabel <-> Do the classification manually
						}
					}
					
					logger.info("Score: " + numCorrect + " / " + show.size());
					System.out.print("\nFeedback end. Train and test again? (0 = NO, 1 = YES):");
					Scanner scanner = new Scanner(System.in);
					doNextFeedback = scanner.nextInt();
					
					if (doNextFeedback == 1) {
						testData.removeAll(show);
						if (testData.size() <= 0) {
							doNextFeedback = 0;
						} else {
							trainData.addAll(show);
							this.writeDataToFile(trainData, testData);
							
							logger.info("*** Do Next Opt-Train-Predict-Feedback Process.");
						}
					}
				}
				
			} while (doNextFeedback == 1);
			
			this.currentTestPass++;
		}		
	}
	
	
	public static final int RUN_DUMP_DATA = 0; 
	public static final int RUN_OPT = 1;
	public static final int RUN_TEST = 2;
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
			case RUN_TEST:
				svm.trainAndTest();
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
	
	// result for cross-validation (CV)
	public static class CV {
		public double accuracy;
		public double meanSquareError;
		public double squareCoefficient;
	}
}
