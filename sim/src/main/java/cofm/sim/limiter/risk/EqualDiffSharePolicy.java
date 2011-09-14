package cofm.sim.limiter.risk;

public class EqualDiffSharePolicy extends AgentRiskSharePolicy {

	@Override
	protected double doCalc(int selectorIndex, int numSelectors) {
		return 2.0 * (numSelectors - selectorIndex + 1) / ((numSelectors + 1) * (numSelectors + 2));
	}

}
