package path.select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import path.Path;
import path.PathIterator;
import path.PathSet;

public class MidLengthPathPolicy implements PathSelectPolicy {

	@Override
	public String toString() {
		return "Middle";
	}
	
	@Override
	public Path selectPath(PathSet pathSet) {
		Collections.sort(pathSet.getPaths(), new Comparator<Path>() {

			@Override
			public int compare(Path arg0, Path arg1) {
				return arg0.length() - arg1.length();
			}
			
		});
		
		return pathSet.getPaths().get((pathSet.getPaths().size() - 1)/ 2);
	}

}
