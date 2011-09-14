package cofm.sim.limiter.risk;

import java.util.Collection;

import cofm.sim.action.Action;
import cofm.sim.action.Creation;
import cofm.sim.action.Selection;
import cofm.sim.action.Waiting;
import cofm.sim.agent.Agent;
import cofm.sim.element.Element;
import cofm.sim.limiter.*;
import cofm.sim.pool.Pool;

public class RiskLimiter extends AbstractLimiter {

	protected ElementRiskPolicy elemRisk;
	protected AgentRiskSharePolicy share;
	
	protected class Risk implements LimiterInfo {
		public double value;
		public Risk(double value) {
			this.value = value;
		}
		public String toString() {
			return "Risk=" + String.format("%.3f", value);
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
			int numSelectorsNow = action.target().getSelectors().size();
			double totalRiskNow = elemRisk.calcRisk(numSelectorsNow, pool.numAgent());
			double totalRiskBefore = elemRisk.calcRisk(numSelectorsNow - 1, pool.numAgent());
			
			// The creator (the "0th" selector)
			Risk cr = (Risk) info.get(action.target().getCreator());
			cr.value += deltaRiskOfSelector(0, numSelectorsNow, totalRiskNow, totalRiskBefore);
			
			// The selectors
			for (int i = 0; i < action.target().getSelectors().size(); i++) {
				Risk cs = (Risk) info.get(action.target().getSelectors().get(i));
				cs.value += deltaRiskOfSelector(i+1, numSelectorsNow, totalRiskNow, totalRiskBefore); 
			}
		}
	}

	protected double deltaRiskOfSelector(int index, int numSelectorsNow, 
			double totalRiskNow, double totalRiskBefore) {
		if (index == numSelectorsNow) {
			// "Before" == 0, just return "Now"
			return share.calcProportion(index, numSelectorsNow) * totalRiskNow;
		}
		// return "Now" - "Before"
		int numSelectorsBefore = numSelectorsNow - 1;
		return share.calcProportion(index, numSelectorsNow) * totalRiskNow -
			share.calcProportion(index, numSelectorsBefore) * totalRiskBefore;
	}

}
