package cofm.sim.limiter.risk;

import cofm.sim.util.CacheManager;

public abstract class AgentRiskSharePolicy extends CacheManager {
	
	protected class IndexAndTotal extends CacheKey {

		private int index;
		private int total;
		
		public IndexAndTotal(int index, int total) {
			this.index = index;
			this.total = total;
		}
		
		@Override
		public int hashCode() {
			return (index << 4 + index) ^ total;
		}

		@Override
		public boolean equals(Object o) {
			if (this == null || o == null) return false;
			if (this == o) return true;
			if (!(o instanceof IndexAndTotal)) return false;
			return this.hashCode() == o.hashCode();
		}
		
	}
	
	public double calcProportion(int selectorIndex, int numSelectors) {
		IndexAndTotal key = new IndexAndTotal(selectorIndex, numSelectors);
		Object prop = this.lookup(key); 
		if (prop == null) {
			Double val = doCalc(selectorIndex, numSelectors);
			this.store(key, val);
			return val;
		}
		return (Double) prop;
	}
	
	abstract protected double doCalc(int selectorIndex, int numSelectors);
}
