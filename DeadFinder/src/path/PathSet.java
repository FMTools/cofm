package path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import path.select.PathSelectPolicy;

import model.Feature;
import model.FeatureModel;
import model.FmReader;


public class PathSet implements Iterable<Path> {
	
	private List<Feature> roots = new ArrayList<Feature>();
	private List<Path> paths = new ArrayList<Path>();

	private Path shortest;
	private Path longest;

	public PathSet(FeatureModel fm) {
		roots.add(fm.getRoot());
	}
	
	public boolean isEmpty() {
		return roots.isEmpty();
	}
	
	public Path selectPath(PathSelectPolicy policy) {
		return policy.selectPath(this);
	}
	
	public void cutNodesInPath(Path path) {
		// The children of ALIVE nodes in the path become new roots.
		for (Feature node: path.getNodes()) {
			if (node.getDead() == Feature.ALIVE) {
				roots.addAll(node.getChildren());
			}
		}
		
		// Roots that appear in the path are removed.
		roots.removeAll(path.getNodes());
	}

	public void setRoots(List<Feature> roots) {
		this.roots = roots;
	}

	public List<Feature> getRoots() {
		return roots;
	}

	@Override
	public Iterator<Path> iterator() {
		return new PathIterator(this);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		PathIterator i = (PathIterator) this.iterator();
		while (i.hasNext()) {
			sb.append(i.next().toString() + "\n");
		}
		return sb.toString();
	}
	
	public void enumeratePaths() {
		longest = null;
		shortest = null;
		paths.clear();
		
		PathIterator i = (PathIterator) this.iterator();
		while (i.hasNext()) {
			Path cur = i.next();
			paths.add(cur);
			if (longest == null || longest.length() < cur.length()) {
				longest = cur;
			}
			if (shortest == null || shortest.length() > cur.length()) {
				shortest = cur;
			}
		}
	}
	
	public List<Path> getPaths() {
		return paths;
	}

	public Path getShortest() {
		return shortest;
	}

	public Path getLongest() {
		return longest;
	}
}
