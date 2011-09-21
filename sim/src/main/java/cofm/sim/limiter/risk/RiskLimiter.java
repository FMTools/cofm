package cofm.sim.limiter.risk;

import java.util.Map.Entry;

import cofm.sim.action.*;
import cofm.sim.agent.Agent;
import cofm.sim.element.Element;
import cofm.sim.limiter.*;
import cofm.sim.pool.Pool;

public class RiskLimiter extends AbstractLimiter {

	protected final int CREATION_INC_STEP = 100;
	protected final double INITIAL_CREATION_GAIN = 0.2;
	protected final double FIRST_SELECTOR_GAIN = 0.2;
	protected final double CREATION_INC_FACTOR = 2;
	protected final int SELECTION_DEC_FACTOR = 2;
	
	
	protected ElementRiskPolicy elemRisk;
	protected AgentRiskSharePolicy share;
	
	protected class Risk implements LimiterInfo {
		public double value;
		public double max;
		public Risk(double value, double initial) {
			this.value = value;
			max = initial;
		}
		public String toString() {
			return "Risk=" + String.format("%.3f", value) + " / " + String.format("%.3f", max);
		}
		public void add(Risk delta) {
			value += delta.value;
			max += delta.max;
		}
	}
	
	protected double initialMax;
	protected double selectThresh;
	
	public RiskLimiter(Pool pool, Double initialMax, Double selectThresh,
			ElementRiskPolicy elemRisk, AgentRiskSharePolicy share) {
		super(pool);
		this.initialMax = initialMax;
		this.selectThresh = selectThresh;
		this.elemRisk = elemRisk;
		this.share = share;
	}

	@Override
	protected LimiterInfo initLimiterInfo(Agent agent) {
		return new Risk(0.0, initialMax);
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
		return risk.value + deltaRisk <= risk.max;
	}

	public void update(Agent owner, Action action) {
		if (action instanceof Creation) {
			LimiterInfo li = info.get(owner);
			Risk risk = (Risk) li;
			risk.value += ElementRiskPolicy.INITIAL_RISK;
		} else if (!(action instanceof Waiting)) {
			int numSelectorsNow = action.target().getSelectors().size();
			int numSelectorsBefore = 0;
			
			if (action instanceof Selection) {
				numSelectorsBefore = numSelectorsNow - 1;
				
				// The selectors
				for (int i = 0; i < numSelectorsNow; i++) {
					Risk cs = (Risk) info.get(action.target().getSelectors().get(i));
					cs.add(deltaRiskOfSelector(action.target(), i+1, i+1, numSelectorsNow, numSelectorsBefore));
				}
				
			} else if (action instanceof Deselect) {
				numSelectorsBefore = numSelectorsNow + 1;
				
				// The de-selector
				Risk dsr = (Risk) info.get(owner);
				int index = ((Deselect) action).getSelectorIndex();
				dsr.add(deltaRiskOfSelector(action.target(), -1, index+1, numSelectorsNow, numSelectorsBefore));
				
				// The selectors in front of the de-selector
				for (int i = 0; i < index; i++) {
					Risk sr1 = (Risk) info.get(action.target().getSelectors().get(i));
					sr1.add(deltaRiskOfSelector(action.target(), i+1, i+1, numSelectorsNow, numSelectorsBefore));
				}
				
				// The selectors after the de-selector
				for (int j = index; j < numSelectorsNow; j++) {
					Risk sr2 = (Risk) info.get(action.target().getSelectors().get(j));
					sr2.add(deltaRiskOfSelector(action.target(), j+1, j+2, numSelectorsNow, numSelectorsBefore));
				}
			}
			
			// The creator (the "0th" selector)
			Risk cr = (Risk) info.get(action.target().getCreator());
			cr.add(deltaRiskOfSelector(action.target(), 0, 0, numSelectorsNow, numSelectorsBefore));
			
		}
	}

	protected Risk deltaRiskOfSelector(Element e, int indexNow, int indexBefore,
			int numSelectorsNow, int numSelectorsBefore) {
		int totalNumOfAgents = pool.numAgent();
		double rBefore, rNow, maxBefore, maxNow;
		
		if (indexBefore < 0 || indexBefore > numSelectorsBefore) {
			// The selector is not here before
			rBefore = 0.0;
			maxBefore = 0.0;
		} else {
			maxBefore = calcSelectorGain(e, indexBefore, numSelectorsBefore);
			rBefore = share.calcProportion(indexBefore, numSelectorsBefore)
			* elemRisk.calcRisk(numSelectorsBefore, totalNumOfAgents);
		}
		
		if (indexNow < 0 || indexNow > numSelectorsNow) {
			// The selector is not here anymore.
			rNow = 0.0;
			maxNow = 0.0;
		} else {
			maxNow = calcSelectorGain(e, indexNow, numSelectorsNow);
			rNow = share.calcProportion(indexNow, numSelectorsNow)
			* elemRisk.calcRisk(numSelectorsNow, totalNumOfAgents);
		}
		
		return new Risk(rNow - rBefore, maxNow - maxBefore);
	}
	
	private double calcCreatorGain(Element e) {
		 return (e.getId() / CREATION_INC_STEP + 1) * CREATION_INC_FACTOR * INITIAL_CREATION_GAIN;
	}
	
	protected double calcSelectorGain(Element e, int selectorIndex, int numSelectors) {
		if (!isQualifiedElement(numSelectors)) {
			return 0;
		}
		// Creator == 0th selector
		if (selectorIndex == 0) {
			return calcCreatorGain(e);
		}
		double gain = FIRST_SELECTOR_GAIN;
		for (int i = 1; i < selectorIndex; i++) {
			gain /= SELECTION_DEC_FACTOR;
		}
		return gain;
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
			cr.max += calcSelectorGain(elem, 0, elem.getSelectors().size());
			
			for (int i = 0; i < elem.getSelectors().size(); i++) {
				Risk sr = (Risk) info.get(elem.getSelectors().get(i));
				sr.value += risk * share.calcProportion(i+1, elem.getSelectors().size());
				sr.max += calcSelectorGain(elem, i+1, elem.getSelectors().size());
			}
		}
	}

	protected void resetLimiterInfo() {
		for (Entry<Agent, LimiterInfo> entry: info.entrySet()) {
			entry.setValue(new Risk(0.0, initialMax));
		}
	}
	
	protected boolean isQualifiedElement(int numSelectors) {
		double rate = pool.numAgent() == 0 ? 0 : numSelectors * 1.0 / pool.numAgent();
		return rate > selectThresh;
	}
}
