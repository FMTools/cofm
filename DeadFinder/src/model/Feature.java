package model;

import java.util.ArrayList;
import java.util.List;

import util.StructureChangedException;

public class Feature {
	
	private int id;
	private boolean dead = false;
	private String name;
	
	private int state;
	
	private Feature parent = null;
	private List<Feature> children = new ArrayList<Feature>();
	
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
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isDead() {
		return dead;
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

}