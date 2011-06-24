package collab.fm.mining.constraint.stats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import collab.fm.mining.constraint.FeaturePair;

public interface DataStats {

	public void update(List<FeaturePair> data);
	public void report(BufferedWriter out) throws IOException;
}
