package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class PopularityFirstPolicy implements SelectionPolicy {

	protected double leastPopularity;
	protected Pool pool;
	protected List<Element> elements;
	protected int index;
	
	public PopularityFirstPolicy(Pool pool, Double atLeast) {
		this.leastPopularity = atLeast;
		this.pool = pool;
	}

	public void loadElements(List<Element> data) {
		elements = data;
		index = elements.size() - 1;
		Collections.sort(elements, new Comparator<Element>(){

			public int compare(Element arg0, Element arg1) {
				return arg0.getSelectors().size() - arg1.getSelectors().size();
			}
			
		});
	}

	public Element selectNext() {
		if (index < 0) {
			return null;
		}
		Element e = elements.get(index--);
		if (e.getSelectors().size() * 1.0 / pool.numAgent() < leastPopularity) {
			return null;
		}
		return e;
	}

}
