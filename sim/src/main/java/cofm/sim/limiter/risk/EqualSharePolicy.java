package cofm.sim.limiter.risk;

public class EqualSharePolicy extends AgentRiskSharePolicy {

	@Override
	protected double doCalc(int selectorIndex, int numSelectors) {
		return 1.0 / (numSelectors + 1);
	}

}
