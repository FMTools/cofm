package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class BadRatingRandomPolicy extends BadRatingFirstPolicy {

	public BadRatingRandomPolicy(Pool pool, Double atMost, Double mustKeep) {
		super(pool, atMost, mustKeep);
	}
	
	public Element selectNextBest() {
		while (indexBest < elements.size()) {
			Element e = elements.get(indexBest++);
			if (e.rating() > highestRating) {
				continue;
			} else {
				return e;
			}
		}
		return null;
	}
	
	public Element selectNextWorst() {
		while (indexWorst >= 0) {
			Element e = elements.get(indexWorst--);
			if (e.rating() < keepRating) {
				continue;
			} else {
				return e;
			}
		}
		return null;
	}
	
	@Override
	public void loadElements(List<Element> data) {
		elements = data;
		Collections.shuffle(elements);
		indexBest = 0;
		indexWorst = elements.size() - 1;
	}

}
