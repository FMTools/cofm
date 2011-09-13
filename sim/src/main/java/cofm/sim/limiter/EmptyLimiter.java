package cofm.sim.limiter;

import cofm.sim.action.Action;
import cofm.sim.agent.Agent;

public class EmptyLimiter implements Limiter {

	public void addAgent(Agent agent) {
		// do nothing
	}

	public boolean isValidAction(Agent owner, Action action) {
		return true;
	}

	public void update(Agent owner, Action action) {
		// do nothing
	}

}
