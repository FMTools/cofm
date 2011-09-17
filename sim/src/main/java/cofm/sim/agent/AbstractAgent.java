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
	
	protected String name; 
	
	public AbstractAgent(Pool pool, Limiter limiter, int id, String name) {
		this.id = id;
		this.name = name;
		lastAction = null;
		lastFailedAction = null;
		this.limiter = limiter;
		this.pool = pool;
		pool.addAgent(this);
	}
	
	@Override
	public Agent clone() {
		return null;
	}
	
	public Action getLastAction() {
		return lastAction;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	public void setId(int id) {
		this.id = id;
	}
	
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
