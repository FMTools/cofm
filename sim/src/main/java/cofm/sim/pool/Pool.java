package cofm.sim.pool;

import java.util.List;

import cofm.sim.agent.Agent;
import cofm.sim.element.Element;
import cofm.sim.limiter.Limiter;

public interface Pool {
	void addAgent(Agent agent);
	int numAgent();
	
	void setLimiter(Limiter limiter);
	
	void addElement(Element element);
	List<Element> listElements();
	
	EndCondition getEndCondition();
	void setEndCondition(EndCondition cond);
	boolean endAfterLastEvolve();
	
	void evolve();
	
}
