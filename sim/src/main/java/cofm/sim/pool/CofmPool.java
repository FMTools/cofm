package cofm.sim.pool;

import java.util.ArrayList;
import java.util.List;

import cofm.sim.agent.Agent;
import cofm.sim.element.Element;
import cofm.sim.element.FmElement;
import cofm.sim.limiter.Limiter;

public class CofmPool implements Pool {

	protected List<Agent> agents = new ArrayList<Agent>();
	protected Limiter limiter;
	protected List<Element> elements = new ArrayList<Element>();
	protected EndCondition endCondition;
	
	public void addAgent(Agent agent) {
		agents.add(agent);
	}
	
	public void setLimiter(Limiter limiter) {
		this.limiter = limiter;
	}
	
	public void evolve() {
		for (Agent agent: agents) {
			agent.executeAction();
		}
	}
	
	public String toString() {
		return "Pool Stats: " + this.numAgent() + " Agent(s), " + this.elements.size() + " Element(s). " +
			"Average Rating = " + toDoubleStr(getAvgElementRating());
	}

	private double getAvgElementRating() {
		if (this.elements.size() <= 0) {
			return 0;
		}
		double total = 0;
		for (Element e: elements) {
			total += e.rating();
		}
		return total / this.elements.size();
	}
	
	private String toDoubleStr(double d) {
		return String.format("%.3f", d);
	}

	public void addElement(Element element) {
		element.setId(elements.size());
		elements.add(element);
	}

	public List<Element> listElements() {
		return elements;
	}

	public int numAgent() {
		return agents.size();
	}

	public void setEndCondition(EndCondition cond) {
		this.endCondition = cond;
	}
	
	public EndCondition getEndCondition() {
		return this.endCondition;
	}

	public boolean endAfterLastEvolve() {
		return this.endCondition.endAfterThisTurn();
	}

}
