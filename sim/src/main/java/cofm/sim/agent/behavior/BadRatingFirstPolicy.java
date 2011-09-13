package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class BadRatingFirstPolicy implements SelectionPolicy {

	protected double highestRating;
	protected List<Element> elements;
	protected int index;
	
	public BadRatingFirstPolicy(Pool pool, Double atMost) {
		// ignore "pool"
		this.highestRating = atMost;
	}
	
	public Element selectNext() {
		if (index >= elements.size()) {
			return null;
		}
		Element e = elements.get(index++);
		if (e.rating() > highestRating) {
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
		index = 0;
	}

}
