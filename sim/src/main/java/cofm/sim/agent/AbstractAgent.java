package cofm.sim.agent;

import org.apache.log4j.Logger;

import cofm.sim.action.Action;
import cofm.sim.limiter.Limiter;
import cofm.sim.pool.Pool;

public abstract class AbstractAgent implements Agent {

	static Logger logger = Logger.getLogger(AbstractAgent.class);
	
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
		prepareExecution();
		do {
			lastAction = nextAction(); 
			lastFailedAction = lastAction;
			
		} while (!limiter.isValidAction(this, lastAction));

		lastAction.execute();
		lastFailedAction = null;
		
		limiter.update(this, lastAction);
		finishExecution();
	}

	abstract protected void prepareExecution();
	abstract protected Action nextAction();
	abstract protected void finishExecution();

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
