package cofm.sim.limiter.risk;

import cofm.sim.limiter.risk.AgentRiskSharePolicy.IndexAndTotal;
import cofm.sim.util.CacheManager;

public abstract class ElementRiskPolicy extends CacheManager {

	public static final double INITIAL_RISK = 1.0;
	
	protected double thresh;
	
	protected class SelectAndTotal extends CacheKey {

		private int select;
		private int total;
		
		public SelectAndTotal(int s, int t) {
			this.select = s;
			this.total = t;
		}
		
		@Override
		public int hashCode() {
			return (select << 5 + select) ^ total;
		}

		@Override
		public boolean equals(Object o) {
			if (this == null || o == null) return false;
			if (this == o) return true;
			if (!(o instanceof SelectAndTotal)) return false;
			return this.hashCode() == o.hashCode();
		}
		
	}
	
	public ElementRiskPolicy(Double thresh) {
		this.thresh = thresh;
	}
	
	public double calcRisk(int numSelectors, int totalNumOfAgents) {
		SelectAndTotal key = new SelectAndTotal(numSelectors, totalNumOfAgents);
		Object risk = this.lookup(key);
		if (risk == null) {
			Double value = doCalc(numSelectors, totalNumOfAgents);
			this.store(key, value);
			return value;
		}
		return (Double) risk;
	}
	
	abstract protected double doCalc(int numSelectors, int totalNumOfAgents);
}
