package cofm.sim.limiter.risk;

public class ExpRiskPolicy extends ElementRiskPolicy {

	protected int steepness;
	public ExpRiskPolicy(Double thresh, Integer steepness) {
		super(thresh);
		this.steepness = steepness;
	}

	@Override
	protected double doCalc(int numSelectors, int totalNumOfAgents) {
		double p = 1.0 * numSelectors / totalNumOfAgents;
		if (p == 0.0) {
			return ElementRiskPolicy.INITIAL_RISK;
		}
		if (p >= thresh) {
			return 0;
		}
		double p0d = Math.pow(thresh, steepness);
		double pd = Math.pow(p, steepness);
		return p0d * (p0d - pd) / (p0d * p0d + pd);
	}

}
