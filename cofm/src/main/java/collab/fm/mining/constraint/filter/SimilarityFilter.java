package collab.fm.mining.constraint.filter;

import collab.fm.mining.constraint.FeaturePair;
import collab.fm.mining.constraint.PairFilter;

public class SimilarityFilter implements PairFilter {

	private double thresh;
	
	public boolean keepPair(FeaturePair pair) {
		return pair.getTotalSim() > thresh;
	}
	
	public SimilarityFilter(double threshold) {
		thresh = threshold;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
