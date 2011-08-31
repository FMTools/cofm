package path.select;

import path.Path;
import path.PathSet;

public interface PathSelectPolicy {

	// Select a path from the path set
	Path selectPath(PathSet pathSet);
}
