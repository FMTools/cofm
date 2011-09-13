package cofm.sim.limiter.risk;

public class EqualSharePolicy extends AgentRiskSharePolicy {

	@Override
	public double calcProportion(int selectorIndex, int numSelectors) {
		return 1.0 / (numSelectors + 1);
	}

}
