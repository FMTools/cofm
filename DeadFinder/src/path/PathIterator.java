package path;

import java.util.Iterator;

import util.TreeTraverseException;

import model.Feature;

public class PathIterator implements Iterator<Path> {
	
	private PathSet pathSet;
	
	private Feature lastVisited;
	
	public PathIterator(PathSet pathSet) {
		this.pathSet = pathSet;
		// Set all nodes as unvisited
		for (Feature root: pathSet.getRoots()) {
			markAsUnvisited(root);
		}
		this.lastVisited = null;
	}
	
	private void markAsUnvisited(Feature root) {
		root.setState(0);  // Visited 0 times
		for (Feature child: root.getChildren()) {
			markAsUnvisited(child);
		}
	}

	@Override
	public boolean hasNext() {
		if (lastVisited == null) {
			return !pathSet.getRoots().isEmpty();
		}
		// Find key-point for next path by backtracking. (It also sets lastVisited to the key point.)
		do {
			if (lastVisited.getState() < 0) {
				// Backtracked to a root. (root.getState == -1 * its_visited_times)
				if (lastVisited.getState() * (-1) >= lastVisited.getChildren().size()) {
					// All children has been visited, so the subtree is finished, 
					// move to next unvisited root
					for (Feature root: pathSet.getRoots()) {
						if (root.getState() == 0) {
							lastVisited = root;
							return true;
						}
					}
					return false;  // All roots are visited, done.
 				} else {
 					// Go to next unvisited children
 					lastVisited = getUnvisitedChild(lastVisited);
 					if (lastVisited == null) {
 						throw new TreeTraverseException("Implementation error! Shouldn't reach here.");
 					}
 					return true;
 				}
			} else if (lastVisited.getState() > 0) {
				// Backtracked to a middle node.
				if (lastVisited.getState() < lastVisited.getChildren().size()) {
					// Go to next unvisited children
					lastVisited = getUnvisitedChild(lastVisited);
					if (lastVisited == null) {
 						throw new TreeTraverseException("Implementation error! Shouldn't reach here.");
 					}
					return true;
				}
			} 
		} while ((lastVisited = lastVisited.getParent()) != null);
		return false;
	}
	
	private Feature getUnvisitedChild(Feature parent) {
		for (Feature child: parent.getChildren()) {
			if (child.getState() == 0) {
				return child;
			}
		}
		return null;
	}

	@Override
	public Path next() {
		Path path = new Path();
		if (lastVisited == null) {
			// The first traverse starts with a root
			lastVisited = pathSet.getRoots().get(0);
		}
		
		// Follow the path down until we reach a leaf.
		while (!lastVisited.getChildren().isEmpty()) {
			lastVisited = lastVisited.getChildren().get(0);
		}
		
		// Visit the leaf 
		increaseVisitCounter(lastVisited);
		
		if (isRoot(lastVisited)) {
			path.addNode(lastVisited);
			return path;
		}
		
		// Now update the visit count (setState()) bottom up, the rule is that
		// if current node is finished, then add 1 to its parent visited count 
		// (The visit count == how many children are finished)
		Feature cur = lastVisited, parent = cur.getParent();
		for (; !isRoot(parent); cur = parent, parent = parent.getParent()) {
			if (isFinished(cur)) {
				increaseVisitCounter(parent);
			}
			path.prependNode(cur);
		}
		if (isFinished(cur)) {
			increaseVisitCounter(parent);
		}
		path.prependNode(cur);
		path.prependNode(parent);
		
		return path;
	}

	private void increaseVisitCounter(Feature f) {
		if (isRoot(f)) {
			f.setState(f.getState() - 1);
		} else {
			f.setState(f.getState() + 1);
		} 
	}

	private boolean isFinished(Feature f) {
		if (f.getState() < 0) {
			return f.getState() * (-1) >= f.getChildren().size();
		}
		return f.getState() >= f.getChildren().size();
	}
	
	private boolean isRoot(Feature f) {
		if (f.getState() < 0) {
			return true;
		}
		for (Feature r: pathSet.getRoots()) {
			if (r == f) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
