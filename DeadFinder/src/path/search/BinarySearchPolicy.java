package path.search;

import model.Feature;
import path.Path;

public class BinarySearchPolicy extends PathSearchPolicy {

	@Override
	public String toString() {
		return "Binary";
	}
	
	@Override
	public Feature findFirstDead(Path path) {
		return binarySearchFirstDead(path, 0, path.length() - 1, false);
	}
	
	protected Feature binarySearchFirstDead(Path path, int begin, int end, boolean reverse) {
		// Stop conditions (one of these):
		//   - last_alive == end
		//   - first_dead == begin
		//   - last_alive == first_dead - 1
		
		int lastAlive = begin - 1, firstDead = end + 1;
		
		while (begin <= end) {
			int mid;
			if (reverse) {
				mid = (begin + end + 1) / 2;
			} else {
				mid = (begin + end) / 2;
			}
			compareCounts++;
			if (path.getNodes().get(mid).isDead()) {
				firstDead = mid;
				if (firstDead == begin || lastAlive == firstDead - 1) {
					return path.getNodes().get(firstDead);
				}
				end = mid - 1;
			} else {
				lastAlive = mid;
				if (lastAlive == end) {
					return null;  
				}
				if (lastAlive == firstDead - 1) {
					return path.getNodes().get(firstDead);
				}
				begin = mid + 1;
			}
		}
		
		return null;
	}
	
}
