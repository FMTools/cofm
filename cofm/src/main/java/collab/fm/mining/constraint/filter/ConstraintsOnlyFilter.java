package collab.fm.mining.constraint.filter;

import collab.fm.mining.constraint.FeaturePair;
import collab.fm.mining.constraint.PairFilter;
import collab.fm.mining.constraint.SVM;

public class ConstraintsOnlyFilter implements PairFilter {

	public boolean keepPair(FeaturePair pair, int mode) {
		if (pair.getLabel() == FeaturePair.NO_CONSTRAINT 
				|| pair.getLabel() == FeaturePair.UNKNOWN) {
		// Skip non-constraint pairs in "train with constraints only" mode
			return (mode != SVM.MODE_TRAIN_ONLY_CON); 
		} 
		return true;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
