package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.element.FmElement;
import cofm.sim.pool.Pool;

public class RatingFirstPolicy implements SelectionPolicy {

	protected double lowestRating;
	protected List<Element> elements;
	protected int index;
	
	public RatingFirstPolicy(Pool pool, Double atLeast) {
		this.lowestRating = atLeast;
	}
	
	public Element selectNext() {
		if (index < 0) {
			return null;
		}
		Element e = elements.get(index--);
		if (e.rating() > lowestRating) {
			return e;
		} 
		return null;
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
		index = elements.size() - 1;
	}

}
