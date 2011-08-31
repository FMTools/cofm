package path.search;

import model.Feature;
import path.Path;

public class LeafLinearSearch extends PathSearchPolicy {

	@Override
	public String toString() {
		return "LeafLinear";
	}
	
	@Override
	public Feature findFirstDead(Path path) {
		for (int i = path.length() - 1; i >= 0; i--) {
			compareCounts++;
			if (!path.getNodes().get(i).isDead()) {
				// Found the last alive feature
				if (i+1 == path.length()) {
					return null;
				} 
				return path.getNodes().get(i+1);
			} 
		}
		return path.getNodes().get(0);
	}

}
