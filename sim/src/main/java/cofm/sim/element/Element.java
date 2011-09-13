package cofm.sim.element;

import java.util.ArrayList;
import java.util.List;

import cofm.sim.agent.Agent;

public abstract class Element {

	protected long id;

	protected List<Agent> selectors = new ArrayList<Agent>();
	protected Agent creator;
	
	public Element(Agent creator) {
		this.creator = creator;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public List<Agent> getSelectors() {
		return selectors;
	}
	
	public Agent getCreator() {
		return creator;
	}
	
	abstract public double rating();
}
