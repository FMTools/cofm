package cofm.sim.agent;

import java.util.ArrayList;
import java.util.List;

import cofm.sim.action.Action;
import cofm.sim.action.Creation;
import cofm.sim.action.Deselect;
import cofm.sim.action.Selection;
import cofm.sim.action.Waiting;
import cofm.sim.agent.behavior.SelectionPolicy;
import cofm.sim.element.Element;
import cofm.sim.element.FmElement;
import cofm.sim.limiter.Limiter;
import cofm.sim.pool.Pool;

public class CofmAgent extends AbstractAgent {

	protected double minRating; // 0 to 1
	protected double maxRating;
	
	// Action preference: [0, probCreate] = create, [probSelect, 1] = select, other = wait
	protected double probCreate;
	protected double probSelect;
	
	protected double probDeselect;
	
	protected boolean creationFailed;
	protected int numCreate = 0;
	protected int numSelect = 0;
	
	// Selection policy
	protected SelectionPolicy selectionPolicy;
	
	public CofmAgent(Pool pool, Limiter limiter, Integer id, String name,
			Double minRating, Double maxRating,
			Double probCreate, Double probSelect, Double probDeselect,
			SelectionPolicy sp) {
		super(pool, limiter, id, name);
		
		this.minRating = minRating;
		this.maxRating = maxRating;
		
		this.probCreate = probCreate;
		this.probSelect = probSelect;
		this.probDeselect = probDeselect;
		
		this.selectionPolicy = sp;
	}

	@Override
	public Agent clone() {
		return new CofmAgent(pool, limiter, id, name,
				minRating, maxRating, probCreate, probSelect, probDeselect, selectionPolicy);
	}
	
	public String toString() {
		return this.getName() + "_" + this.getId();
	}
	
	@Override
	protected Action nextAction() {
		boolean canCreateOrSelect = true;
		if (lastFailedAction != null && lastFailedAction instanceof Creation) {
			// If Creation failed once, it will always fail in this turn.
			creationFailed = true;
		}
		if (lastFailedAction != null && lastFailedAction instanceof Deselect) {
			canCreateOrSelect = false;
		}
		
		if (canCreateOrSelect) {
			double r = (creationFailed ? genRandomIn(probCreate, 1.0) : Math.random());
			
			if (r < probCreate) {
				return new Creation(pool, createElement());
			} else if (r > probSelect) {
				Action sel = trySelection();
				if (sel != null) {
					return sel;
				}
			} else {
				return new Waiting();
			}
		}
		
		// If we reach here, it means both creation and selection are invalid, 
		// so we try deselection 
		if (Math.random() < probDeselect) {
			Action des = tryDeselect();
			if (des != null) {
				return des;
			}
		}
		
		// At last we have to wait.
		return new Waiting();
	}
	
	// Generate random number inside [begin, end]
	protected double genRandomIn(double begin, double end) {
		return begin + Math.random() * (end - begin);
	}
	
	protected Element createElement() {
		// New ID = pool.number_of_elements
		return new FmElement(this, pool.listElements().size(), genRandomIn(minRating, maxRating));
	}

	protected Action tryDeselect() {
		Element elem = null;
		if (lastFailedAction == null || !(lastFailedAction instanceof Deselect)) {
			// This is the first running in current turn.
			List<Element> candidates = new ArrayList<Element>();
			for (Element e: pool.listElements()) {
				if (e.getSelectors().contains(this)) {
					candidates.add(e);
				}
			}
			selectionPolicy.loadElements(candidates);
		}
		
		elem = selectionPolicy.selectNextWorst();
		
		if (elem == null) {
			return null;
		}
		return new Deselect(pool, elem, this);
	}
	
	protected Action trySelection() {
		Element elem = null;
		if (lastFailedAction == null || !(lastFailedAction instanceof Selection)) {
			// This is the first running in current turn.
			List<Element> candidates = new ArrayList<Element>();
			for (Element e: pool.listElements()) {
				if (e.isPlaceholder() || 
						e.getCreator().equals(this) || e.getSelectors().contains(this)) {
					continue;
				}
				candidates.add(e);
			}
			selectionPolicy.loadElements(candidates);
		}
		
		elem = selectionPolicy.selectNextBest();
		
		if (elem == null) {
			return null;
		}
		return new Selection(pool, elem, this);
	}

	@Override
	protected void finishExecution() {
		if (lastAction instanceof Creation) {
			numCreate++;
		} else if (lastAction instanceof Selection) {
			numSelect++;
		} else if (lastAction instanceof Deselect) {
			numSelect--;
		}
	}

	@Override
	protected void prepareExecution() {
		creationFailed = false;
	}
	
	public int getNumCreate() {
		return numCreate;
	}
	
	public int getNumSelect() {
		return numSelect;
	}
}
