package model;

import java.util.ArrayList;
import java.util.List;

public class FeatureModel {

	public static final int END_OF_DEAD = 1;
	public static final int HAS_NEXT_DEAD = 0;
	
	private int numFeatures = 0;
	private int numLevels = 0;
	
	private String name;
	private Feature root;
	private Feature theDead;
	private int currentDeadIndex = 0;
	
	private List<Feature> levelTraverseList = new ArrayList<Feature>();
	
	public String toString() {
		return printTree(root, 0) + numFeatures + " feature(s).";
	}
	
	private String printTree(Feature r, int level) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < level; i++) {
			s.append("\t");
		}
		s.append(r.toString() + "\n");
		for (Feature child: r.getChildren()) {
			s.append(printTree(child, level+1));
		}
		return s.toString();
	}

	/**
	 * Mark all feature as alive
	 */
	public void markAllAsAlive() {
		markSubTreeAs(false, root);
	}
	
	/**
	 * Initialize the 1-dead-feature structure, i.e. the first child of the root is dead and that's 
	 * the only dead feature in the feature model
	 */
	public void initOneDeadStructure() {
		markAllAsAlive();
		calcLevelTraverseList();
		currentDeadIndex = 1;   // 1 because we want to skip the root. (The root can never be dead in an actual feature model.)
		theDead = null;
	}
	
	public int nextOneDeadStructure() {
		// Change the current dead feature (and its descendants) to "alive"
		markSubTreeAs(false, theDead);
		
		// Set the next dead feature (according to the level traverse list)
		if (levelTraverseList.size() > currentDeadIndex) {
			theDead = levelTraverseList.get(currentDeadIndex++);
			markSubTreeAs(true, theDead);
			return HAS_NEXT_DEAD;
		} else {
			theDead = null;
			return END_OF_DEAD;
		}
		
	}
	
	private void markSubTreeAs(boolean deadState, Feature subTreeRoot) {
		if (subTreeRoot == null) {
			return;
		}
		
		subTreeRoot.setDead(deadState);
		
		for (Feature child: subTreeRoot.getChildren()) {
			markSubTreeAs(deadState, child);
		}
	}
	
	private void calcLevelTraverseList() {
		levelTraverseList.clear();
		levelTraverseList.add(root);
		int pos = 0;
		do {
			Feature cur = levelTraverseList.get(pos++);
			levelTraverseList.addAll(cur.getChildren());
		} while (levelTraverseList.size() > pos);
	}
	
	public void setRoot(Feature root) {
		this.root = root;
	}

	public Feature getRoot() {
		return root;
	}

	public void setTheDead(Feature theDead) {
		this.theDead = theDead;
	}

	public Feature getTheDead() {
		return theDead;
	}

	public void setLevelTraverseList(List<Feature> levelTraverseList) {
		this.levelTraverseList = levelTraverseList;
	}

	public List<Feature> getLevelTraverseList() {
		return levelTraverseList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumLevels(int numLevels) {
		this.numLevels = numLevels;
	}

	public int getNumLevels() {
		return numLevels;
	}
	
}
