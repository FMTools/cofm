package cofm.sim.action;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public abstract class AbstractAction implements Action {

	protected Pool pool;
	protected Element element;

	public AbstractAction(Pool pool, Element element) {
		this.pool = pool;
		this.element = element;
	}
	
	public Element target() {
		return element;
	}
}
