package cofm.sim.agent.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cofm.sim.element.Element;
import cofm.sim.pool.Pool;

public class PopularityFirstPolicy implements SelectionPolicy {

	protected double leastPopularity, keepPopularity;
	protected Pool pool;
	protected List<Element> elements;
	protected int indexBest, indexWorst;
	
	public PopularityFirstPolicy(Pool pool, Double atLeast, Double mustKeep) {
		this.leastPopularity = atLeast;
		this.keepPopularity = mustKeep;
		this.pool = pool;
	}

	public void loadElements(List<Element> data) {
		elements = data;
		indexBest = elements.size() - 1;
		indexWorst = 0;
		Collections.sort(elements, new Comparator<Element>(){

			public int compare(Element arg0, Element arg1) {
				return arg0.getSelectors().size() - arg1.getSelectors().size();
			}
			
		});
	}

	public Element selectNextBest() {
		if (indexBest < 0) {
			return null;
		}
		Element e = elements.get(indexBest--);
		if (e.getSelectors().size() * 1.0 / pool.numAgent() > leastPopularity) {
			return e;
		}
		return null;
	}
	
	public Element selectNextWorst() {
		if (indexWorst > elements.size() - 1) {
			return null;
		}
		Element e = elements.get(indexWorst++);
		if (e.getSelectors().size() * 1.0 / pool.numAgent() > keepPopularity) {
			return null;
		}
		return e;
	}

}
