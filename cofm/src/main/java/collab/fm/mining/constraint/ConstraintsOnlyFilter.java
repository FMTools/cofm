package collab.fm.mining.constraint;

public class ConstraintsOnlyFilter implements PairFilter {

	public boolean keepPair(FeaturePair pair, int mode) {
		if (pair.getLabel() == FeaturePair.NO_CONSTRAINT 
				|| pair.getLabel() == FeaturePair.UNKNOWN) {
		// Skip non-constraint pairs in "train with constraints only" mode
			return (mode != SVM.MODE_TRAIN_ONLY_CON); 
		} 
		return true;
	}

}
