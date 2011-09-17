package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.element.FmElement;
import cofm.sim.pool.Pool;

public class RatingFirstPolicy implements SelectionPolicy {

	protected double lowestRating, keepRating;
	protected List<Element> elements;
	protected int indexBest, indexWorst;
	
	public RatingFirstPolicy(Pool pool, Double atLeast, Double mustKeep) {
		this.lowestRating = atLeast;
		this.keepRating = mustKeep;
	}
	
	public Element selectNextBest() {
		if (indexBest < 0) {
			return null;
		}
		Element e = elements.get(indexBest--);
		if (e.rating() > lowestRating) {
			return e;
		} 
		return null;
	}
	
	public Element selectNextWorst() {
		if (indexWorst >= elements.size()) {
			return null;
		}
		Element e = elements.get(indexWorst++);
		if (e.rating() > keepRating) {
			// No give up any elements that must be kept
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
		indexBest = elements.size() - 1;
		indexWorst = 0;
	}

}
