package path.search;

import model.Feature;
import path.Path;

public class LeafBinarySearchPolicy extends BinarySearchPolicy {

	@Override
	public String toString() {
		return "LeafBinary";
	}
	
	@Override
	public Feature findFirstDead(Path path) {
		compareCounts++;
		if (!path.getNodes().get(path.length() - 1).isDead()) {
			// If the leaf is alive, then the whole path is alive
			return null;
		}
		Feature dead = binarySearchFirstDead(path, 0, path.length() - 2, true);
		if (dead == null) {
			return path.getNodes().get(path.length() - 1);
		} else {
			return dead;
		}
	}
}
