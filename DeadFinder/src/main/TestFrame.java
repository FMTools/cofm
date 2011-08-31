package main;

import java.util.Map;

import model.FeatureModel;
import model.FmReader;

import org.apache.log4j.Logger;

import path.search.*;
import path.select.*;

public class TestFrame {

	private static final String[] fmFiles = {
		"53.xml"
		,"94.xml"
		,"290.xml"
	};
	
	static Logger logger = Logger.getLogger(TestFrame.class);
	
	private StringBuilder report = new StringBuilder();
	
	private TestRunner runner = new TestRunner();
	
	// Format:
	//   FM_Name  Feature_Num  Level_Num  Path_Search  Path_Select  0D_CC  1D_MIN  1D_MAX  1D_AVG  1D_VAR
	private final String DATA_FORMAT = "%20s%10s%10s%12s%12s%10s%13s%13s%13s%13s\n";
	
	public static void main(String[] args) {
		logger.info("    START: (" + fmFiles.length + ") Feature Models");
		
		TestFrame frame = new TestFrame();
		frame.printHeader();
		
		for (String file: TestFrame.fmFiles) {
			frame.runOnFile(file);
		}
		
		logger.info("Result:\n" + frame.getReportString());
	}
	
	public void runOnFile(String fmFile) {
		FeatureModel fm = new FmReader().readFromSplot(fmFile);
		
		runOnFM(fm, new BinarySearchPolicy(), new LongestPathPolicy(), true, true, true);
		runOnFM(fm, new BinarySearchPolicy(), new MidLengthPathPolicy(), false, false, true);
		runOnFM(fm, new BinarySearchPolicy(), new ShortestPathPolicy(), false, false, true);
		
		runOnFM(fm, new LeafBinarySearchPolicy(), new LongestPathPolicy(), false, true, true);
		runOnFM(fm, new LeafBinarySearchPolicy(), new MidLengthPathPolicy(), false, false, true);
		runOnFM(fm, new LeafBinarySearchPolicy(), new ShortestPathPolicy(), false, false, true);
		
		runOnFM(fm, new RootBinarySearchPolicy(), new LongestPathPolicy(), false, true, true);
		runOnFM(fm, new RootBinarySearchPolicy(), new MidLengthPathPolicy(), false, false, true);
		runOnFM(fm, new RootBinarySearchPolicy(), new ShortestPathPolicy(), false, false, true);
		
		runOnFM(fm, new LeafLinearSearch(), new LongestPathPolicy(), false, true, true);
		runOnFM(fm, new LeafLinearSearch(), new MidLengthPathPolicy(), false, false, true);
		runOnFM(fm, new LeafLinearSearch(), new ShortestPathPolicy(), false, false, true);
		
		runOnFM(fm, new RootLinearSearch(), new LongestPathPolicy(), false, true, true);
		runOnFM(fm, new RootLinearSearch(), new MidLengthPathPolicy(), false, false, true);
		runOnFM(fm, new RootLinearSearch(), new ShortestPathPolicy(), false, false, true);
		
		report.append("\n");
	}
	
	public void printHeader() {
		report.append("\n");
		report.append(String.format(DATA_FORMAT, 
				"Feature Model", "Feature#", "Max Level", 
				"Path Search", "Path Select", "0D_CC",
				"1D_CC_MIN", "1D_CC_MAX", "1D_CC_AVG", "1D_CC_VAR"));
		report.append("-----------------------------------------------------------------------------");
		report.append("-----------------------------------------------------------------------------\n");
	}
	
	public String getReportString() {
		return report.toString();
	}
	
	private void runOnFM(FeatureModel fm, PathSearchPolicy searcher, PathSelectPolicy selector,
			boolean showFmInfo, boolean showSearcher, boolean showSelector) {
		runner.startNewRun(fm, searcher, selector);
		Map<String, Double> data = runner.reportCounts();
		report.append(String.format(DATA_FORMAT, 
				(showFmInfo ? fm.getName() : " "),
				(showFmInfo ? fm.getNumFeatures() + "" : " "),
				(showFmInfo ? fm.getNumLevels() + "" : " "),
				(showSearcher ? searcher.toString() : " "),
				(showSelector ? selector.toString() : " "),
				toIntStr(data.get(TestRunner.DEAD_0_COUNTS)),
				toIntStr(data.get(TestRunner.DEAD_1_MIN_COUNTS)),
				toIntStr(data.get(TestRunner.DEAD_1_MAX_COUNTS)),
				toDoubleStr(data.get(TestRunner.DEAD_1_AVG_COUNTS)),
				toDoubleStr(data.get(TestRunner.DEAD_1_VARIANCE))
				));
	}
	
	private String toIntStr(Double d) {
		return d.intValue() + "";
	}
	
	private String toDoubleStr(Double d) {
		return String.format("%.2f", d);
	}
}
