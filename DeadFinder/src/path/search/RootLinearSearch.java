package path.search;

import model.Feature;
import path.Path;

public class RootLinearSearch extends PathSearchPolicy {

	@Override
	public String toString() {
		return "RootLinear";
	}
	
	@Override
	public Feature findFirstDead(Path path) {
		for (int i = 0; i < path.length(); i++) {
			compareCounts++;
			if (path.getNodes().get(i).isDead()) {
				// Found the first dead
				return path.getNodes().get(i);
			}
		}
		return null;
	}

}
