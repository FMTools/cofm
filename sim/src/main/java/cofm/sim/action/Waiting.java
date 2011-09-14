package cofm.sim.action;

import cofm.sim.element.Element;

public class Waiting implements Action {

	public void execute() {
		// Do nothing
	}

	public String toString() {
		return "WAIT";
	}
	
	public Element target() {
		return null;
	}

}
