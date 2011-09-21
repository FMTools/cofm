package cofm.sim.element;

import cofm.sim.agent.Agent;

public class FmElement extends Element {

	protected double rating;  
	
	public FmElement(Agent creator, int id, double rating) {
		super(creator, id);
		this.rating = rating;
	}
	
	public double rating() {
		return rating;
	}
}
