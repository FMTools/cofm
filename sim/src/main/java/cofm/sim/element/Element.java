package cofm.sim.element;

import java.util.ArrayList;
import java.util.List;

import cofm.sim.agent.Agent;

public abstract class Element {

	protected int id;

	protected List<Agent> selectors = new ArrayList<Agent>();
	protected Agent creator;
	
	public Element(Agent creator, int id) {
		this.creator = creator;
		setId(id);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public List<Agent> getSelectors() {
		return selectors;
	}
	
	public Agent getCreator() {
		return creator;
	}
	
	public boolean isPlaceholder() {
		return creator == null;
	}
	
	abstract public double rating();
}
