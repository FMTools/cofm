package cofm.sim.limiter.risk;

public class RectRiskPolicy extends ElementRiskPolicy {

	public RectRiskPolicy(Double thresh) {
		super(thresh);
	}

	@Override
	protected double doCalc(int numSelectors, int totalNumOfAgents) {
		if (numSelectors * 1.0 / totalNumOfAgents < thresh) {
			return ElementRiskPolicy.INITIAL_RISK;
		}
		return 0;
	}

}
