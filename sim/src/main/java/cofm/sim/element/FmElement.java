package cofm.sim.element;

import cofm.sim.agent.Agent;

public class FmElement extends Element {

	protected double rating;  
	
	public FmElement(Agent creator, double rating) {
		super(creator);
		this.rating = rating;
	}
	
	public double rating() {
		return rating;
	}
}
