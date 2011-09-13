package cofm.sim.agent;

import cofm.sim.action.Action;
import cofm.sim.limiter.Limiter;
import cofm.sim.pool.Pool;

public abstract class AbstractAgent implements Agent {

	protected int id;
	protected Limiter limiter;
	protected Pool pool;
	protected Action lastAction;
	protected Action lastFailedAction;
	
	public AbstractAgent(Pool pool, Limiter limiter, int id) {
		this.id = id;
		lastAction = null;
		lastFailedAction = null;
		this.limiter = limiter;
		this.pool = pool;
		pool.addAgent(this);
		limiter.addAgent(this);
	}
	
	public void executeAction() {
		do {
			lastAction = nextAction(); 
			lastFailedAction = lastAction;
		} while (!limiter.isValidAction(this, lastAction));

		lastAction.execute();
		lastFailedAction = null;
		
		limiter.update(this, lastAction);
	}

	abstract protected Action nextAction();

	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return this.getId();
	}
	
	public boolean equals(Object o) {
		if (o == null || this == null) return false;
		if (this == o) return true;
		if (!(o instanceof AbstractAgent)) return false;
		return this.getId() == ((AbstractAgent)o).getId();
	}
}
