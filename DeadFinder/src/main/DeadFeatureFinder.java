package main;

import java.util.ArrayList;
import java.util.List;

import path.*;
import path.search.PathSearchPolicy;
import path.select.PathSelectPolicy;

import model.Feature;
import model.FeatureModel;

public class DeadFeatureFinder {

	public class Result {
		public List<Feature> deadFeatures = new ArrayList<Feature>();
		public int compareCounts;
	}
	
	private PathSearchPolicy pathSearcher;
	private PathSelectPolicy pathSelector;
	
	public DeadFeatureFinder(PathSearchPolicy searchPolicy, PathSelectPolicy selectPolicy) {
		this.pathSearcher = searchPolicy;
		this.pathSelector = selectPolicy;
	}
	
	public Result findOne(FeatureModel fm) {
		return find(fm, 1);
	}
	
	/**
	 * Find primary dead features (i.e. the roots of dead subtrees), the procedure will stop if
	 * one of the conditions is satisfied:
	 *  - "howMany" primary dead features are found
	 *  - All features are checked, in this case the Result.deadFeatures contains actual primary dead features
	 * 
	 * @param fm The feature model
	 * @param howMany How many primary-dead-features in the FM at most?
	 * @return
	 */
	public Result find(FeatureModel fm, int howMany) {
		Result result = new Result();
		
		pathSearcher.resetCounts();
		int howManyAreFound = 0;
		
		PathSet pathSet = new PathSet(fm);
		while (!pathSet.isEmpty()) {
			Path path = pathSelector.selectPath(pathSet);
			Feature dead = null;
			if ((dead = pathSearcher.findFirstDead(path)) != null) {
				result.deadFeatures.add(dead);
				if (++howManyAreFound >= howMany) {
					break;
				}
			}
			pathSet.cutNodesInPath(path);
		}
		
		result.compareCounts = pathSearcher.getCompareCounts();
		
		return result;
	}
}
