package cofm.sim.limiter.risk;

public abstract class AgentRiskSharePolicy {
	
	abstract public double calcProportion(int selectorIndex, int numSelectors);
}
