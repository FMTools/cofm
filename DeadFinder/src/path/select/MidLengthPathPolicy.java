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
	
	private List<Path> paths = new ArrayList<Path>();
	
	@Override
	public Path selectPath(PathSet pathSet) {
		paths.clear();
		PathIterator i = (PathIterator) pathSet.iterator();
		while (i.hasNext()) {
			paths.add(i.next());
		}
		
		Collections.sort(paths, new Comparator<Path>() {

			@Override
			public int compare(Path arg0, Path arg1) {
				return arg0.length() - arg1.length();
			}
			
		});
		
		return paths.get((paths.size() - 1)/ 2);
	}

}
