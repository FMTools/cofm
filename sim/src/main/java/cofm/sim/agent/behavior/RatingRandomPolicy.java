package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class RatingRandomPolicy extends RatingFirstPolicy {

	public RatingRandomPolicy(Pool pool, Double atLeast, Double mustKeep) {
		super(pool, atLeast, mustKeep);
	}

	public Element selectNextBest() {
		while (indexBest >= 0) {
			Element e = elements.get(indexBest--);
			if (e.rating() > lowestRating) {
				return e;
			} 
		}
		return null;
	}
	
	public Element selectNextWorst() {
		while (indexWorst < elements.size()) {
			Element e = elements.get(indexWorst++);
			if (e.rating() > keepRating) {
				// No give up any elements that must be kept
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
		
		indexBest = elements.size() - 1;
		indexWorst = 0;
	}

}
