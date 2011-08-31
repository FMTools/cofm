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
		PathIterator i = (PathIterator) pathSet.iterator();
		Path result = null;
		while (i.hasNext()) {
			Path cur = i.next();
			if (result == null || result.length() < cur.length()) {
				result = cur;
			}
		}
		return result;
	}

}
