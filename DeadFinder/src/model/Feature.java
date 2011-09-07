package model;

import java.util.ArrayList;
import java.util.List;

import util.StructureChangedException;

public class Feature {
	
	public static final int DEAD = 0;
	public static final int ALIVE = 1;
	public static final int UNKNOWN = -1;
	
	private FeatureModel fm;
	
	private int id;
	private int dead = UNKNOWN;
	private String name;
	
	private int state;
	
	private int traverseIndex;
	
	private Feature parent = null;
	private List<Feature> children = new ArrayList<Feature>();
	
	public Feature(FeatureModel fm) {
		this.fm = fm;
	}
	
	public boolean isDead() {
		return this.getFm().getChecker().isDead(this);
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == null || that == null) {
			return false;
		}
		if (this == that) {
			return true;
		}
		if (!(that instanceof Feature)) {
			return false;
		}
		Feature f = (Feature) that;
		return this.getId() == f.getId();
	}
	
	public Feature(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String toString() {
		return getName() + "(" + (isDead() ? "0" : "1") + ")";
	}
	
	public void addChild(Feature child) {
		children.add(child);
		child.setParent(this);
	}
	
	public boolean isDescendantOf(Feature another) {
		if (this == another) {
			return true;
		}
		for (Feature f = this; f != null; f = f.getParent()) {
			if (f == another) {
				return true;
			}
		}
		return false;
	}
	
	public int getDead() {
		return this.dead;
	}
	
	public void setDead(int dead) {
		this.dead = dead;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Feature getParent() {
		return parent;
	}
	public void setParent(Feature parent) {
		this.parent = parent;
	}
	public List<Feature> getChildren() {
		return children;
	}
	public void setChildren(List<Feature> children) {
		this.children = children;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setTraverseIndex(int traverseIndex) {
		this.traverseIndex = traverseIndex;
	}

	public int getTraverseIndex() {
		return traverseIndex;
	}

	public FeatureModel getFm() {
		return fm;
	}

}