package cofm.sim.action;

import cofm.sim.agent.Agent;
import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class Deselect extends AbstractAction {

	protected int selectorIndex;
	protected Agent agent;
	
	public Deselect(Pool pool, Element element, Agent agent) {
		super(pool, element);
		this.agent = agent;
	}

	public String toString() {
		return "DESELECT";
	}
	
	public void execute() {
		for (int i = 0; i < element.getSelectors().size(); i++) {
			if (this.agent.equals(element.getSelectors().get(i))) {
				selectorIndex = i;
				element.getSelectors().remove(i);
				return;
			}
		}
		selectorIndex = -1;
	}
	
	public int getSelectorIndex() {
		return selectorIndex;
	}

}
