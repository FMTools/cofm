package cofm.sim.agent.behavior;

import java.util.List;

import cofm.sim.element.Element;

public interface SelectionPolicy {

	Element selectNext();
	void loadElements(List<Element> data);
}
