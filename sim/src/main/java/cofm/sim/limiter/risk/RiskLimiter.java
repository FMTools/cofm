package cofm.sim.limiter.risk;

import cofm.sim.action.Action;
import cofm.sim.action.Creation;
import cofm.sim.action.Selection;
import cofm.sim.action.Waiting;
import cofm.sim.agent.Agent;
import cofm.sim.element.Element;
import cofm.sim.limiter.AbstractLimiter;
import cofm.sim.pool.Pool;

public class RiskLimiter extends AbstractLimiter {

	protected ElementRiskPolicy elemRisk;
	protected AgentRiskSharePolicy share;
	
	protected class Risk implements LimiterInfo {
		public double value;
		public Risk(double value) {
			this.value = value;
		}
	}
	
	protected double maxRisk;
	
	public RiskLimiter(Pool pool, Double maxRisk, ElementRiskPolicy elemRisk, AgentRiskSharePolicy share) {
		super(pool);
		this.maxRisk = maxRisk;
		this.elemRisk = elemRisk;
		this.share = share;
	}

	@Override
	protected LimiterInfo initLimiterInfo(Agent agent) {
		return new Risk(0.0);
	}

	public boolean isValidAction(Agent owner, Action action) {
		if (action instanceof Waiting) {
			return true;
		}
		
		double deltaRisk = 0;
		if (action instanceof Creation) {
			deltaRisk = ElementRiskPolicy.INITIAL_RISK;
		} else if (action instanceof Selection) {
			// Treat the Owner as a new selector
			Element target = action.target();
			int futureNumSelector = target.getSelectors().size() + 1;
			deltaRisk = elemRisk.calcRisk(futureNumSelector, pool.numAgent()) * 
				share.calcProportion(futureNumSelector - 1, futureNumSelector);
		}
		
		LimiterInfo li = info.get(owner);
		Risk risk = (Risk) li;
		return risk.value + deltaRisk <= maxRisk;
	}

	public void update(Agent owner, Action action) {
		if (action instanceof Creation) {
			LimiterInfo li = info.get(owner);
			Risk risk = (Risk) li;
			risk.value += ElementRiskPolicy.INITIAL_RISK;
		} else if (action instanceof Selection) {
			int numSelectors = action.target().getSelectors().size();
			double totalRisk = elemRisk.calcRisk(numSelectors, pool.numAgent());
			
			// The creator (the "0th" selector)
			Risk cr = (Risk) info.get(action.target().getCreator());
			cr.value += share.calcProportion(0, numSelectors) * totalRisk;
			
			// The selectors
			for (int i = 0; i < action.target().getSelectors().size(); i++) {
				Risk cs = (Risk) info.get(action.target().getSelectors().get(i));
				cs.value += share.calcProportion(i+1, numSelectors) * totalRisk; 
			}
		}
	}

}
