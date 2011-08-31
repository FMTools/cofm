package path.search;

import model.Feature;
import path.Path;

public class RootBinarySearchPolicy extends BinarySearchPolicy {

	@Override
	public String toString() {
		return "RootBinary";
	}
	
	@Override
	public Feature findFirstDead(Path path) {
		compareCounts++;
		if (path.getNodes().get(0).isDead()) {
			// If the root is dead, then the whole path is dead
			return path.getNodes().get(0);
		}
		return binarySearchFirstDead(path, 1, path.length() - 1, false);
	}
}
