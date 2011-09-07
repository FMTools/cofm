package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import main.TestFrame;

import org.apache.log4j.Logger;

import checker.DeadCheckPolicy;

public class FeatureModel {

	static Logger logger = Logger.getLogger(FeatureModel.class);
	
	public static final int END_OF_DEAD = 1;
	public static final int HAS_NEXT_DEAD = 0;
	
	private DeadCheckPolicy checker;
	
	private int numFeatures = 0;
	private int numLevels = 0;
	
	private int totalHeight = 0;
	private int numLeaves = 0;
	private double avgHeight = -1.0;
	
	private String name;
	private Feature root;
	
	private List<Clause> constraints = new ArrayList<Clause>();
	
	private boolean forceEnd = false;
	
	private List<Feature> theDead = null;
	
	private List<Integer> currentDeadIndex = null;
	
	private int numDead;
	
	private List<Feature> levelTraverseList = new ArrayList<Feature>();
	
	public void addConstraint(Clause c) {
		constraints.add(c);
	}
	
	public String printConstraints() {
		StringBuilder sb = new StringBuilder();
		for (Clause c: constraints) {
			sb.append(c.toString() + "\n");
		}
		return sb.toString();
	}
	
	public String toString() {
		return printTree(root, 0) + numFeatures + " feature(s).\n" +
				printConstraints() + constraints.size() + " constraint(s).";
	}
	
	public double getAvgHeight() {
		return avgHeight;
	}
	
	public void calcAvgHeight() {
		totalHeight = 0;
		numLeaves = 0;
		
		sumHeight(root, 1);
		
		avgHeight = totalHeight * 1.0 / numLeaves;
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


	private void sumHeight(Feature r, int rHeight) {
		if (r.getChildren().isEmpty()) {
			totalHeight += rHeight;
			numLeaves++;
		} else {
			for (Feature child: r.getChildren()) {
				sumHeight(child, rHeight+1);
			}
		}
	}

	private void calcLevelTraverseList() {
		levelTraverseList.clear();
		levelTraverseList.add(root);
		int pos = 0;
		do {
			Feature cur = levelTraverseList.get(pos);
			cur.setTraverseIndex(pos);
			pos++;
			levelTraverseList.addAll(cur.getChildren());
		} while (levelTraverseList.size() > pos);
	}
	
	/**
	 * Mark all feature as alive
	 */
	public void markAllAsAlive() {
		markSubTreeAs(Feature.ALIVE, root);
	}
	
	/**
	 * Initialize the 1-dead-feature structure, i.e. the first child of the root is dead and that's 
	 * the only dead feature in the feature model
	 */
	public void initDeadStructure(int numDead) {
		markAllAsAlive();
		
		forceEnd = false;
		
		if (numDead > 0) {
			calcLevelTraverseList();
			this.numDead = numDead;
			theDead = new ArrayList<Feature>(numDead);
			currentDeadIndex = new ArrayList<Integer>(numDead);
			for (int i = 1; i <= numDead; i++) {
				currentDeadIndex.add(0);  //Starts with [1, 2, ..., numDead]
				theDead.add(root);
			}
		} else {
			theDead = null;
			currentDeadIndex = null;
		}
	}
	
	public int nextDeadStructure() {
		if (!forceEnd && numDead == 0) {
			forceEnd = true; // force to end at the next time.
			return HAS_NEXT_DEAD;
		}
		if (theDead == null || currentDeadIndex == null) { // No Dead Features
			return END_OF_DEAD;
		}
		// Change the current dead feature (and its descendants) to "alive"
		Calendar begin = Calendar.getInstance();
		for (Feature d: theDead) {
			markSubTreeAs(Feature.ALIVE, d); 
		}
		
		int checkFrom = 0;
		do {
			checkFrom = nextIndexSequence();
			if (checkFrom < 0) {
				TestFrame.nextSequenceTime += Calendar.getInstance().getTimeInMillis() - begin.getTimeInMillis();
				return END_OF_DEAD;
			}
		} while (!isValidDeadSequence(checkFrom));
		
		for (Feature d: theDead) {
			markSubTreeAs(Feature.DEAD, d);
		}
		TestFrame.nextSequenceTime += Calendar.getInstance().getTimeInMillis() - begin.getTimeInMillis();
		return HAS_NEXT_DEAD;
	}
	
	private boolean isValidDeadSequence(int checkFrom) {
		for (int i = checkFrom; i < numDead; i++) {
			for (int j = 0; j < checkFrom; j++) {
				if (levelTraverseList.get(currentDeadIndex.get(i)).isDescendantOf(
						levelTraverseList.get(currentDeadIndex.get(j)))) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void updateDead(int pos, int nodeIndex) {
		currentDeadIndex.set(pos, nodeIndex);
		theDead.set(pos, levelTraverseList.get(nodeIndex));
	}
	
	// return the first changing position
	private int nextIndexSequence() {
		if (currentDeadIndex.get(0) == 0) {
			for (int i = 0; i < numDead; i++) {
				updateDead(i, i+1);   // start with [1, 2, 3...]
			}
			return 0;
		}
		int pos = numDead - 1;
		do {
			int next = currentDeadIndex.get(pos) + 1;
			if (next < levelTraverseList.size() - (numDead - pos - 1)) {
				// It is a valid sequence
				for (int i = pos, j = 0; i < numDead; i++, j++) {
					updateDead(i, next + j);
				}
				return pos;
			}
		} while (--pos >= 0);
		return -1;
	}

	private void markSubTreeAs(int deadState, Feature subTreeRoot) {
		if (subTreeRoot == null) {
			return;
		}
		
		subTreeRoot.setDead(deadState);
		
		for (Feature child: subTreeRoot.getChildren()) {
			markSubTreeAs(deadState, child);
		}
	}
	
	public void setRoot(Feature root) {
		this.root = root;
	}

	public Feature getRoot() {
		return root;
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

	public void setConstraints(List<Clause> constraints) {
		this.constraints = constraints;
	}

	public List<Clause> getConstraints() {
		return constraints;
	}

	public void setChecker(DeadCheckPolicy checker) {
		this.checker = checker;
	}

	public DeadCheckPolicy getChecker() {
		return checker;
	}
	
}
