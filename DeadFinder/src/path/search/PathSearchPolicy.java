package path.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Feature;

import path.Path;

public abstract class PathSearchPolicy {
	
	protected int compareCounts = 0;
	
	/**
	 * Find the highest (i.e. the first from the list's head) dead feature in the path.
	 * @param path
	 * @return The dead feature, null if not found.
	 */
	abstract public Feature findFirstDead(Path path);
	
	public void resetCounts() {
		compareCounts = 0;
	}
	
	public int getCompareCounts() {
		return compareCounts;		
	}
	
	 
}
