package cofm.sim.limiter.risk;

import java.util.Map.Entry;

import cofm.sim.action.*;
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
		if (action instanceof Waiting || action instanceof Deselect) {
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
		} else {
			int totalNumOfAgents = pool.numAgent();
			int numSelectorsNow = action.target().getSelectors().size();
			int numSelectorsBefore = 0;
			
			if (action instanceof Selection) {
				numSelectorsBefore = numSelectorsNow - 1;
				
				// The selectors
				for (int i = 0; i < numSelectorsNow; i++) {
					Risk cs = (Risk) info.get(action.target().getSelectors().get(i));
					cs.value += deltaRiskOfSelector(i+1, i+1, numSelectorsNow, numSelectorsBefore, totalNumOfAgents); 
				}
				
			} else if (action instanceof Deselect) {
				numSelectorsBefore = numSelectorsNow + 1;
				
				// The de-selector
				Risk dsr = (Risk) info.get(owner);
				int index = ((Deselect) action).getSelectorIndex();
				dsr.value += deltaRiskOfSelector(-1, index, numSelectorsNow, numSelectorsBefore, totalNumOfAgents);
				
				// The selectors in front of the de-selector
				for (int i = 0; i < index; i++) {
					Risk sr1 = (Risk) info.get(action.target().getSelectors().get(i));
					sr1.value += deltaRiskOfSelector(i+1, i+1, numSelectorsNow, numSelectorsBefore, totalNumOfAgents);
				}
				
				// The selectors after the de-selector
				for (int j = index; j < numSelectorsNow; j++) {
					Risk sr2 = (Risk) info.get(action.target().getSelectors().get(j));
					sr2.value += deltaRiskOfSelector(j+1, j+2, numSelectorsNow, numSelectorsBefore, totalNumOfAgents);
				}
			}
			
			// The creator (the "0th" selector)
			Risk cr = (Risk) info.get(action.target().getCreator());
			cr.value += deltaRiskOfSelector(0, 0, numSelectorsNow, numSelectorsBefore, totalNumOfAgents);
			
		}
	}

	protected double deltaRiskOfSelector(int indexNow, int indexBefore, 
			int numSelectorsNow, int numSelectorsBefore, 
			int totalNumOfAgents) {
		double before, now;
		if (indexBefore < 0 || indexBefore > numSelectorsBefore) {
			// The selector is not here before
			before = 0.0;
		} else {
			before = share.calcProportion(indexBefore, numSelectorsBefore)
					* elemRisk.calcRisk(numSelectorsBefore, totalNumOfAgents);
		}
		if (indexNow < 0 || indexNow > numSelectorsNow) {
			// The selector is not here anymore.
			now = 0.0; 
		} else {
			now = share.calcProportion(indexNow, numSelectorsNow)
					* elemRisk.calcRisk(numSelectorsNow, totalNumOfAgents);
		}
		
		return now - before;
	}

	@Override
	protected void updateOnAgentNumChanged() {
		// Recalculate all limiter info
		resetLimiterInfo();
		
		for (Element elem: pool.listElements()) {
			if (elem.isPlaceholder()) {
				continue;
			}
			
			double risk = elemRisk.calcRisk(elem.getSelectors().size(), pool.numAgent());
			
			Risk cr = (Risk) info.get(elem.getCreator());
			cr.value += risk * share.calcProportion(0, elem.getSelectors().size());
			
			for (int i = 0; i < elem.getSelectors().size(); i++) {
				Risk sr = (Risk) info.get(elem.getSelectors().get(i));
				sr.value += risk * share.calcProportion(i+1, elem.getSelectors().size());
			}
		}
	}

	protected void resetLimiterInfo() {
		for (Entry<Agent, LimiterInfo> entry: info.entrySet()) {
			entry.setValue(new Risk(0.0));
		}
	}
}
