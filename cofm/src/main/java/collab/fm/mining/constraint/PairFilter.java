package collab.fm.mining.constraint;

public interface PairFilter {

	public boolean keepPair(FeaturePair pair);
	
	public void dispose();
	
}
