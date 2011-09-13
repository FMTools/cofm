package cofm.sim.limiter.risk;

public class EqualDiffSharePolicy extends AgentRiskSharePolicy {

	@Override
	public double calcProportion(int selectorIndex, int numSelectors) {
		return 2.0 * (numSelectors - selectorIndex + 1) / ((numSelectors + 1) * (numSelectors + 2));
	}

}
