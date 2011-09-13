package cofm.sim.action;

import cofm.sim.element.Element;

public interface Action {
	void execute();
	
	Element target();
}
