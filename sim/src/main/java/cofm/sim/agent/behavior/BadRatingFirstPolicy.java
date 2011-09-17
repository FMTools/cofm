package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class BadRatingFirstPolicy implements SelectionPolicy {

	protected double highestRating, keepRating;
	protected List<Element> elements;
	protected int indexBest, indexWorst;
	
	public BadRatingFirstPolicy(Pool pool, Double atMost, Double mustKeep) {
		// ignore "pool"
		this.highestRating = atMost;
		this.keepRating = mustKeep;
	}
	
	public Element selectNextBest() {
		if (indexBest >= elements.size()) {
			return null;
		}
		Element e = elements.get(indexBest++);
		if (e.rating() > highestRating) {
			return null;
		}
		return e;
	}
	
	public Element selectNextWorst() {
		if (indexWorst < 0) {
			return null;
		}
		Element e = elements.get(indexWorst--);
		if (e.rating() < keepRating) {
			return null;
		}
		return e;
	}
	
	public void loadElements(List<Element> data) {
		elements = data;
		Collections.sort(elements, new Comparator<Element>() {

			public int compare(Element arg0, Element arg1) {
				if (arg0.rating() < arg1.rating()) {
					return -1;
				} else if (arg0.rating() > arg1.rating()) {
					return 1;
				} 
				return 0;
			}
			
		});
		indexBest = 0;
		indexWorst = elements.size() - 1;
	}

}
