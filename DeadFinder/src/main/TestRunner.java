package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import path.search.PathSearchPolicy;
import path.select.PathSelectPolicy;

import model.FeatureModel;

/**
 * Run DeadFeatureFinder (the finder) several times on a specific feature model, including:
 *  - Run the finder ONCE, on a feature model WITHOUT any dead features.
 *  - Run the finder (Feature_model_size - 1) times, on a feature model with only 1 dead subtree.
 *  
 * @author Li Yi
 *
 */
public class TestRunner {

	// DEAD_0 means no dead features
	public static final String DEAD_0_COUNTS = "0_dead_counts";
	
	// DEAD_1 means there is 1 dead feature
	public static final String DEAD_1_MIN_COUNTS = "1_dead_min_counts";
	public static final String DEAD_1_MAX_COUNTS = "1_dead_max_counts";
	public static final String DEAD_1_AVG_COUNTS = "1_dead_avg_counts";
	public static final String DEAD_1_VARIANCE = "1_dead_variance";
	
	private int deadZeroCounts;
	private List<Integer> deadOneCounts = new ArrayList<Integer>();
	
	public void startNewRun(FeatureModel model, 
							PathSearchPolicy searchPolicy, 
							PathSelectPolicy selectPolicy) {
		
		DeadFeatureFinder finder = new DeadFeatureFinder(searchPolicy, selectPolicy);
		
		// Run 0-dead test
		model.markAllAsAlive();
		deadZeroCounts = finder.findOne(model).compareCounts;
		
		// Run 1-dead tests
		deadOneCounts.clear();
		model.initOneDeadStructure();
		while(model.nextOneDeadStructure() != FeatureModel.END_OF_DEAD) {
			deadOneCounts.add(finder.findOne(model).compareCounts);
		}
	}
	
	public Map<String, Double> reportCounts() {
		Collections.sort(deadOneCounts);
		
		int min = deadOneCounts.get(0), max = deadOneCounts.get(deadOneCounts.size() - 1);
		
		int total = 0;
		for (Integer i: deadOneCounts) {
			total += i;
		}
		double avg = total * 1.0 / deadOneCounts.size();
		
		double variance = 0.0;
		for (Integer i: deadOneCounts) {
			double gap = i - avg;
			variance += gap * gap;
		}
		variance /= deadOneCounts.size();
		variance = Math.sqrt(variance);
		
		Map<String, Double> result = new HashMap<String, Double>();
		result.put(DEAD_0_COUNTS, Double.valueOf(deadZeroCounts));
		result.put(DEAD_1_AVG_COUNTS, Double.valueOf(avg));
		result.put(DEAD_1_MAX_COUNTS, Double.valueOf(max));
		result.put(DEAD_1_MIN_COUNTS, Double.valueOf(min));
		result.put(DEAD_1_VARIANCE, Double.valueOf(variance));
		
		return result;
	}
}
