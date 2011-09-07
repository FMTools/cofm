package main;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import model.Feature;
import model.FeatureModel;
import model.FmReader;

import org.apache.log4j.Logger;

import checker.DeadCheckPolicy;
import checker.PreloadedDeadStatePolicy;

import path.Path;
import path.PathSet;
import path.search.*;
import path.select.*;

public class TestFrame {

	public static long nextSequenceTime = 0L;
	public static long deadFinderTime = 0L;

	private static final String[] fmFiles = {
		"53.xml"
		//,"94.xml"
//	,"290.xml"
		//"chain_100.xml"
		//"1000-0-1.00-SAT-2.xml"
		//"1000-2-branch.xml"
	};
	
	public void resetTime() {
		nextSequenceTime = 0L;
		deadFinderTime = 0L;
	}
	
	private PathSearchPolicy[] searchPolicies = {
			new BinarySearchPolicy(), 
			new LeafBinarySearchPolicy(),
			new LeafLinearSearch()
			,
			new RootBinarySearchPolicy(),
			new RootLinearSearch()
	};
	
	private PathSelectPolicy[] selectPolicies = {
		new LongestPathPolicy()
		,
		new MidLengthPathPolicy(),
		new ShortestPathPolicy()
	};
	
	private DeadCheckPolicy checkPolicy = new PreloadedDeadStatePolicy();
	
	static Logger logger = Logger.getLogger(TestFrame.class);
	
	private StringBuilder report = new StringBuilder();
	
	private class Counter {
		private int size = 0;
		private int _total = 0;
		private int _min = -1;
		private int _max = -1;
		
		public void add(int num) {
			size++; 
			_total += num;
			if (_min == -1 || _min > num) {
				_min = num;
			}
			if (_max == -1 || _max < num) {
				_max = num;
			}
		}
		
		public int min() { return _min; }
		public int max() { return _max; }
		public double avg() { return (size == 0 ? Double.NaN : _total * 1.0 / size); }
		public int total() { return _total; }
		
	}
	
	private class Summary {
		private Map<String, Counter> counterMap = new HashMap<String, Counter>();
		
		private String getKey(PathSearchPolicy searchPolicy, PathSelectPolicy selectPolicy) {
			return searchPolicy.toString() + "-" + selectPolicy.toString();
		}
		
		public void add(PathSearchPolicy searchPolicy, PathSelectPolicy selectPolicy, int count) {
			String key = getKey(searchPolicy, selectPolicy);
			Counter c = counterMap.get(key);
			if (c == null) {
				c = new Counter();
				counterMap.put(key, c);
			}
			c.add(count);
		}
		
		public Counter get(PathSearchPolicy searchPolicy, PathSelectPolicy selectPolicy) {
			return counterMap.get(getKey(searchPolicy, selectPolicy));
		}
	}
	
	private List<Summary> summaries = new ArrayList<Summary>();
	
	// Format:
	//   FM_Name  Feature_Num  Level  Path_Search  Path_Select  0D  1D(avg)  2D 3D...
	private final String COL_HEAD_FORMAT = "%20s%10s%15s%12s%12s";
	private final String DATA_FORMAT = "%10s";
	
	private void printSummaries(FeatureModel fm, int maxNumDead) {
		boolean showFm = true;
		for (PathSearchPolicy p1: searchPolicies) {
			boolean showP1 = true;
			for (PathSelectPolicy p2: selectPolicies) {
				
				report.append(String.format(COL_HEAD_FORMAT, 
				(showFm ? fm.getName().substring(0, 20) : " "),
				(showFm ? fm.getNumFeatures() + "" : " "),
				(showFm ? toDoubleStr(fm.getAvgHeight()) + "/" + fm.getNumLevels() + "" : " "),
				(showP1 ? p1.toString() : " "),
				p2.toString()));
				
				for (int i = 0; i <= maxNumDead; i++) {
					Counter c = summaries.get(i).get(p1, p2);
					report.append(String.format(DATA_FORMAT, 
							//toDoubleStr(c.avg())));
							c.min()));
				}
				
				report.append("\n");
				
				showFm = false;
				showP1 = false;
			}
		}
		
	}
	
	public void printRowHeader(int maxNumDead) {
		report.append("\n");
		report.append(String.format(COL_HEAD_FORMAT, 
				"Feature Model", "Feature#", "Level(a/m)", 
				"Path Search", "Path Select"));
		for (int i = 0; i <= maxNumDead; i++) {
			report.append(String.format(DATA_FORMAT, i + "_Dead"));
		}
		report.append("\n");
		report.append("-----------------------------------------------------------------------------");
		report.append("-----------------------------------------------------------------------------\n");
	}
	
	public void runOnFile(String fmFile, int maxNumDead) {
		FeatureModel fm = new FmReader().readFromSplot(fmFile);
		fm.setChecker(checkPolicy);
		
		summaries.clear();
		
		for (int i = 0; i <= maxNumDead; i++) {
			summaries.add(new Summary());
			fm.initDeadStructure(i);
			while (fm.nextDeadStructure() != FeatureModel.END_OF_DEAD) {
				Calendar begin = Calendar.getInstance();
			
//				for (PathSearchPolicy p1: searchPolicies) {
//					for (PathSelectPolicy p2: selectPolicies) {
//						p1.resetCounts();
//						int deadFound = 0;
//						PathSet pathSet = new PathSet(fm);
//						Feature d = null;
//						while (!pathSet.isEmpty()) {
//							pathSet.enumeratePaths();
//							Path path = p2.selectPath(pathSet);
//							//logger.info(p1.toString() + " try on path: " + path.toString());
//							if ((d = p1.findFirstDead(path)) != null) {
////								if (i != 0 && ++deadFound >= i) {
////									break;
////								}
//							}
//							pathSet.cutNodesInPath(path);
//						}
//						summaries.get(i).add(p1, p2, p1.getCompareCounts());
//					//	dbg += p1.toString() + "=" + p1.getCompareCounts() + "(" + (d == null ? "null" : d.getName()) + ")  ";
//					}
//				}

				for (PathSelectPolicy selector: selectPolicies) {
					int deadFound = 0;
					int[] counts = new int[searchPolicies.length];
					for (int k = 0; k < counts.length; k++) {
						counts[k] = 0;
					}

					PathSet pathSet = new PathSet(fm);
					while (!pathSet.isEmpty()) {
						pathSet.enumeratePaths();
						Path path = selector.selectPath(pathSet);
						boolean hasDeadInPath = false;
						for (int j = 0; j < searchPolicies.length; j++) {
							PathSearchPolicy searcher = searchPolicies[j];
							searcher.resetCounts();
							if (searcher.findFirstDead(path) != null) {
								hasDeadInPath = true;
							}
							counts[j] += searcher.getCompareCounts();

						}
//						if (i != 0 && hasDeadInPath && ++deadFound >= i) {
//							break;
//						}
						pathSet.cutNodesInPath(path);
					}

					for (int j = 0; j < searchPolicies.length; j++) {
						summaries.get(i).add(searchPolicies[j], selector, counts[j]);
					}
				}

				Calendar end = Calendar.getInstance();
				TestFrame.deadFinderTime += end.getTimeInMillis() - begin.getTimeInMillis();
			}
		}
		
		printSummaries(fm, maxNumDead);

	}
	
	public String getReportString() {
		return report.toString();
	}
		
	private String toDoubleStr(Double d) {
		String s = String.format("%.2f", d);
		
		// delete trailing zeros
		while (s.charAt(s.length() - 1) == '0' || s.charAt(s.length() - 1) == '.') {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}
	
	public static void main(String[] args) {
		logger.info("    START: " + fmFiles.length + " Feature Model(s)");
		
		int maxDeadNum = 3;
		
		TestFrame frame = new TestFrame();
		frame.resetTime();
		frame.printRowHeader(maxDeadNum);
		
		for (String file: TestFrame.fmFiles) {
			frame.runOnFile(file, maxDeadNum);
		}
		
		logger.info("Time on finding next sequence: " + (nextSequenceTime * 1.0 / 1000) + " seconds.");
		logger.info("Time on finding dead features: " + (deadFinderTime * 1.0 / 1000) + " seconds.");
		logger.info("Result:\n" + frame.getReportString());
	}
	
}
