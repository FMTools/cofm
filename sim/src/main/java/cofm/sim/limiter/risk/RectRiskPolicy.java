package cofm.sim.limiter.risk;

public class RectRiskPolicy extends ElementRiskPolicy {

	public RectRiskPolicy(Double thresh) {
		super(thresh);
	}

	@Override
	public double calcRisk(int numSelectors, int totalNumOfAgents) {
		if (numSelectors * 1.0 / totalNumOfAgents < thresh) {
			return ElementRiskPolicy.INITIAL_RISK;
		}
		return 0;
	}

}
