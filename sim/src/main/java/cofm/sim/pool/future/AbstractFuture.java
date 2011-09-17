package cofm.sim.pool.future;

import cofm.sim.pool.Pool;

public abstract class AbstractFuture implements Future {

	protected Pool pool;
	protected int mode;
	protected int turn;
	protected int curTurn;
	
	public AbstractFuture(Pool pool, Integer mode, Integer turn) {
		this.pool = pool;
		this.mode = mode;
		this.turn = turn;
		curTurn = 0;
	}

	public void changePool() {
		++curTurn;
		if ((mode == MODE_REPEAT && curTurn % turn == 0) 
				|| (mode == MODE_ONCE && curTurn == turn)) {
			makeChange();
		}
	}
	
	abstract protected void makeChange();
	
}
