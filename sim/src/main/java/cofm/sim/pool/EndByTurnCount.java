package cofm.sim.pool;

public class EndByTurnCount implements EndCondition {

	protected int maxTurn;
	protected int currentTurn;
	public EndByTurnCount(Pool pool, Integer maxTurn) {
		this.maxTurn = maxTurn;
		currentTurn = 0;
	}
	
	public String toString() {
		return "END AFTER " + maxTurn + " TURNS";
	}
	
	public boolean endAfterThisTurn() {
		return ++currentTurn > maxTurn;
	}

}
