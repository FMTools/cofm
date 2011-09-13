package cofm.sim.pool;

public class EndByPoolSize implements EndCondition {

	protected int maxSize;
	protected Pool pool;
	
	public EndByPoolSize(Pool pool, Integer size) {
		this.maxSize = size;
		this.pool = pool;
	}
	
	public String toString() {
		return "END AFTER " + maxSize + " ELEMENTS ARE CREATED";
	}
	
	public boolean endAfterThisTurn() {
		return pool.listElements().size() > maxSize;
	}

}
