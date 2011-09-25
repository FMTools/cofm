package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class PopularityRandomPolicy extends PopularityFirstPolicy {

	public PopularityRandomPolicy(Pool pool, Double atLeast, Double mustKeep) {
		super(pool, atLeast, mustKeep);
	}
	
	public void loadElements(List<Element> data) {
		elements = data;
		indexBest = elements.size() - 1;
		indexWorst = 0;
		Collections.shuffle(elements);
	}

	public Element selectNextBest() {
		while (indexBest >= 0) {
			Element e = elements.get(indexBest--);
			if (e.getSelectors().size() * 1.0 / pool.numAgent() > leastPopularity) {
				return e;
			}
		}
		return null;
	}
	
	public Element selectNextWorst() {
		while (indexWorst < elements.size()) {
			Element e = elements.get(indexWorst++);
			if (e.getSelectors().size() * 1.0 / pool.numAgent() > keepPopularity) {
				continue;
			} else {
				return e;
			}
		}
		return null;
	}

}
