package cofm.sim.action;

import cofm.sim.agent.Agent;
import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class Creation extends AbstractAction {
	
	public Creation(Pool p, Element e) {
		super(p, e);
	}
	
	public String toString() {
		return "CREATE";
	}
	
	public void execute() {
		pool.addElement(element);
	}

}
