package cofm.sim.limiter.risk;

public class LinearRiskPolicy extends ElementRiskPolicy {

	public LinearRiskPolicy(Double thresh) {
		super(thresh);
	}

	@Override
	protected double doCalc(int numSelectors, int totalNumOfAgents) {
		double p = numSelectors * 1.0 / totalNumOfAgents;
		double r = -1 * p / thresh + 1;
		if (r < 0.0) {
			r = 0;
		}
		return r;
	}

}
