package cofm.sim.agent.behavior;

import java.util.List;

import cofm.sim.element.Element;

public interface SelectionPolicy {

	Element selectNextBest();
	Element selectNextWorst();
	void loadElements(List<Element> data);
}
