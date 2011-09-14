package cofm.sim.action;

import cofm.sim.agent.Agent;
import cofm.sim.agent.behavior.SelectionPolicy;
import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class Selection extends AbstractAction {

	protected Agent agent;
	
	public Selection(Pool pool, Element e, Agent agent) {
		super(pool, e);
		this.agent = agent;
	}

	public String toString() {
		return "SELECT";
	}
	
	public void execute() {
		element.getSelectors().add(agent);
	}

}
