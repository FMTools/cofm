package cofm.sim.pool;

import java.util.ArrayList;
import java.util.List;

import cofm.sim.action.Action;
import cofm.sim.agent.Agent;
import cofm.sim.agent.CofmAgent;
import cofm.sim.element.Element;
import cofm.sim.element.FmElement;
import cofm.sim.limiter.Limiter;
import cofm.sim.limiter.Limiter.LimiterInfo;
import cofm.sim.pool.future.Future;

public class CofmPool implements Pool {

	protected List<Agent> agents = new ArrayList<Agent>();
	protected List<Agent> tracker = new ArrayList<Agent>();
	protected Limiter limiter;
	protected List<Element> elements = new ArrayList<Element>();
	protected EndCondition endCondition;
	protected List<Future> futureEvents = new ArrayList<Future>();
	
	public void addAgent(Agent agent) {
		agents.add(agent);
		limiter.addAgent(agent);
	}
	
	public void setLimiter(Limiter limiter) {
		this.limiter = limiter;
	}
	
	public void evolve() {
		for (Agent agent: agents) {
			agent.executeAction();
		}
		for (Future future: futureEvents) {
			future.changePool();
		}
	}
	
	public String toString() {
		return "Pool Stats: " + this.numAgent() + " Agent(s), " + this.elements.size() + " Element(s). " +
			"Average Rating = " + toDoubleStr(getAvgElementRating()) + 
			"\n" + trackerToString();
	}

	private String trackerToString() {
		StringBuilder sb = new StringBuilder("Tracker: [ ");
		for (int i = 0; i < tracker.size(); i++) {
			CofmAgent agent = (CofmAgent) tracker.get(i);
			LimiterInfo info = limiter.getAgentInfo(agent);
			Action last = agent.getLastAction();
			sb.append((i == 0 ? "" : ", ") + agent.getName() + 
					"(" + (last == null ? "" : last.toString()) +
					(info == null ? "" : ((last == null ? "" : ", " ) + info.toString())) + ")");
		}
		sb.append(" ]");
		return sb.toString();
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

	public void addToTracker(Agent agent) {
		tracker.add(agent);
	}

	public List<Agent> getTracker() {
		return tracker;
	}

	public void addFutureEvent(Future future) {
		futureEvents.add(future);
	}

}
