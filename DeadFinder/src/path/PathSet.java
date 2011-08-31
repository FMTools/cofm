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
			if (!node.isDead()) {
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
	
	public static void main(String[] args) {
		// Test path iterator
		FeatureModel fm = new FmReader().readFromSplot("290.xml");
		fm.initOneDeadStructure();
		fm.nextOneDeadStructure();
		
		System.out.println(fm.toString());
		PathSet paths = new PathSet(fm);
		System.out.println(paths.toString());
		
		PathIterator i = (PathIterator) paths.iterator();
		Path p = i.next();
		System.out.println("Cut by path: " + p.toString());
		paths.cutNodesInPath(p);
		System.out.println(paths.toString());
	}
}
