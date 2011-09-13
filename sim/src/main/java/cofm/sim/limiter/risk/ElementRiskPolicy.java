package cofm.sim.limiter.risk;

public abstract class ElementRiskPolicy {

	public static final double INITIAL_RISK = 1.0;
	
	protected double thresh;
	
	public ElementRiskPolicy(Double thresh) {
		this.thresh = thresh;
	}
	
	abstract public double calcRisk(int numSelectors, int totalNumOfAgents);
}
