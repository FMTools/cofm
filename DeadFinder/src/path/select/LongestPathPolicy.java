package path.select;

import path.Path;
import path.PathIterator;
import path.PathSet;

public class LongestPathPolicy implements PathSelectPolicy {

	@Override
	public String toString() {
		return "Longest";
	}
	
	@Override
	public Path selectPath(PathSet pathSet) {
		return pathSet.getLongest();
	}

}
