package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Feature;
import model.FeatureModel;
import model.FmReader;

import org.apache.log4j.Logger;

import path.Path;
import path.PathSet;
import path.search.BinarySearchPolicy;
import path.search.LeafBinarySearchPolicy;
import path.search.LeafLinearSearch;
import path.search.PathSearchPolicy;
import path.search.RootBinarySearchPolicy;
import path.search.RootLinearSearch;
import path.select.LongestPathPolicy;
import path.select.MidLengthPathPolicy;
import path.select.PathSelectPolicy;
import path.select.ShortestPathPolicy;
import checker.DeadCheckPolicy;
import checker.PreloadedDeadStatePolicy;
import checker.SATCheckPolicy;

public class DeadFinder {
	private static Logger logger = Logger.getLogger(DeadFinder.class);
	
	public static final String[] fmFiles = {
		
	};
	
	public static void main(String[] args) {
		logger.info("    START: " + fmFiles.length + " feature model(s).");
		
		DeadFinder finder = new DeadFinder();
		logger.info(" Checker is " + finder.getChecker().toString());
		
		finder.printHeader();

		for (String file: DeadFinder.fmFiles) {
			finder.checkFm(file);
		}
		
		logger.info("Result:\n" + finder.getReport());
	}

	
	
	private PathSearchPolicy[] searchPolicies = {
			new BinarySearchPolicy(), 
			new LeafBinarySearchPolicy(),
			new LeafLinearSearch(),
			new RootBinarySearchPolicy(),
			new RootLinearSearch()
	};
	
	private PathSelectPolicy[] selectPolicies = {
		new LongestPathPolicy(),
		new MidLengthPathPolicy(),
		new ShortestPathPolicy()
	};
	
	private DeadCheckPolicy checkPolicy = new SATCheckPolicy();
	
	private Map<String, Integer> countMap = new HashMap<String, Integer>();
	
	private List<Feature> deadFeatures = new ArrayList<Feature>();
	
	private StringBuilder report = new StringBuilder();
	
	private String getMapKey(PathSearchPolicy searcher, PathSelectPolicy selecter) {
		return searcher.toString() + "-" + selecter.toString();
	}
	
	private void addCount(PathSearchPolicy searcher, PathSelectPolicy selecter, int count) {
		String key = getMapKey(searcher, selecter);
		Integer i = countMap.get(key);
		if (i == null) {
			i = new Integer(0);
		}
		countMap.put(key, i + count);
	}
	
	private int getCount(PathSearchPolicy searcher, PathSelectPolicy selecter) {
		return countMap.get(getMapKey(searcher, selecter));
	}
	
	// Report format: FM Feature# Leaf# Level Search_policy Select_policy Checked# Dead# Dead_feature_list
	private final String FORMAT = "%-20s%-10s%-10s%-15s%-12s%-12s%-10s%-10s%-30s";
	
	public void printHeader() {
		report.append("\n").append(String.format(FORMAT, 
				"Feature Model", "Feature#", "Leaf#", "Level(a/m)", "Search", "Select", "Checked#", "Dead#", "Dead Feature List"))
			  .append("\n")
			  .append("-----------------------------------------------------------------------------")
		      .append("-----------------------------------------------------------------------------\n");
	}
	
	private void printResult(FeatureModel fm) {
		boolean showFm = true;
		for (PathSearchPolicy p1: searchPolicies) {
			boolean showP1 = true;
			for (PathSelectPolicy p2: selectPolicies) {
				report.append(String.format(FORMAT, 
						(showFm ? fm.getName().substring(0, 20) : " "),
						(showFm ? fm.getNumFeatures() + "" : " "),
						(showFm ? fm.getNumLevels() + "" : " "),
						(showFm ? toDoubleStr(fm.getAvgHeight()) + "/" + fm.getNumLevels() : " "),
						(showP1 ? p1.toString() : " "),
						p2.toString(),
						getCount(p1, p2) + "",
						deadFeatures.size() + "",
						toList(deadFeatures)));
				
				showFm = false;
				showP1 = false;
			}
		}
	}
	
	private String toDoubleStr(Double d) {
		String s = String.format("%.2f", d);
		
		// delete trailing zeros
		while (s.charAt(s.length() - 1) == '0' || s.charAt(s.length() - 1) == '.') {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}
	
	private String toList(List<Feature> o) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < o.size(); i++) {
			sb.append((i == 0 ? "" : ", ") + o.get(i).getName());
		}
		return sb.toString();
	}
		
	public void checkFm(String fmFile) {
		FeatureModel fm = new FmReader().readFromSplot(fmFile);
		fm.setChecker(checkPolicy);
		
		deadFeatures.clear();
		countMap.clear();
		
		for (PathSelectPolicy selecter: selectPolicies) {
			PathSet pathSet = new PathSet(fm);
			while (!pathSet.isEmpty()) {
				pathSet.enumeratePaths();
				Path path = selecter.selectPath(pathSet);
				Feature dead = null;
				for (PathSearchPolicy searcher: searchPolicies) {
					searcher.resetCounts();
					dead = searcher.findFirstDead(path);
					addCount(searcher, selecter, searcher.getCompareCounts());
				}
				
				if (dead != null) {
					deadFeatures.add(dead);
				}
				path.updateByDead(dead);
				pathSet.cutNodesInPath(path);
			}
		}
		
		printResult(fm);
	}
	
	public String getReport() {
		return report.toString();
	}
	
	public DeadCheckPolicy getChecker() {
		return checkPolicy;
	}
	
}
