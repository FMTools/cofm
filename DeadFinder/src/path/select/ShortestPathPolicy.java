package path.select;

import path.Path;
import path.PathIterator;
import path.PathSet;

public class ShortestPathPolicy implements PathSelectPolicy {

	@Override
	public String toString() {
		return "Shortest";
	}
	
	@Override
	public Path selectPath(PathSet pathSet) {
		return pathSet.getShortest();
	}

}
