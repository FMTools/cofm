package cofm.sim.agent;

import java.util.ArrayList;
import java.util.List;

import cofm.sim.action.Action;
import cofm.sim.action.Creation;
import cofm.sim.action.Selection;
import cofm.sim.action.Waiting;
import cofm.sim.agent.behavior.SelectionPolicy;
import cofm.sim.element.Element;
import cofm.sim.element.FmElement;
import cofm.sim.limiter.Limiter;
import cofm.sim.pool.Pool;

public class CofmAgent extends AbstractAgent {

	protected double minMorality; // 0 to 1
	protected double minTalent; // 0 to 1
	protected double maxMorality;
	protected double maxTalent;
	
	// Action preference: [0, probCreate] = create, [probSelect, 1] = select, other = wait
	protected double probCreate;
	protected double probSelect;
	protected double probWait;
	
	// Selection policy
	protected SelectionPolicy selectionPolicy;
	
	public CofmAgent(Pool pool, Limiter limiter, Integer id, 
			Double minMorality, Double maxMorality, Double minTalent, Double maxTalent,
			Double probCreate, Double probSelect, SelectionPolicy sp) {
		super(pool, limiter, id);
		
		this.minMorality = minMorality;
		this.minTalent = minTalent;
		this.maxMorality = maxMorality;
		this.maxTalent = maxTalent;
		
		this.probCreate = probCreate;
		this.probSelect = probSelect;
		probWait = 1 - probCreate - probSelect;
		
		this.selectionPolicy = sp;
	}

	@Override
	protected Action nextAction() {
		boolean canCreate = true;
		if (lastFailedAction != null && lastFailedAction instanceof Creation) {
			// If Creation failed once, it will always fail in this turn.
			canCreate = false;
		}
		double r = (canCreate ? Math.random() : genRandomIn(probCreate, 1.0));
		
		if (r < probCreate) {
			return new Creation(pool, createElement());
		} else if (r > probSelect) {
			return trySelection();
		} else {
			return new Waiting();
		}
	}
	
	// Generate random number inside [begin, end]
	protected double genRandomIn(double begin, double end) {
		return begin + Math.random() * (end - begin);
	}
	
	protected Element createElement() {
		double morality = genRandomIn(minMorality, maxMorality);
		double talent = genRandomIn(minTalent, maxTalent);
		return new FmElement(this, morality * talent);
	}

	protected Action trySelection() {
		Element elem = null;
		if (lastFailedAction == null) {
			// This is the first running in current turn.
			List<Element> candidates = new ArrayList<Element>();
			for (Element e: pool.listElements()) {
				if (e.getCreator().equals(this) || e.getSelectors().contains(this)) {
					continue;
				}
				candidates.add(e);
			}
			selectionPolicy.loadElements(candidates);
		}
		
		elem = selectionPolicy.selectNext();
		
		if (elem == null) {
			return new Waiting();
		}
		return new Selection(pool, elem, this);
	}
}
