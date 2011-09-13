package cofm.sim.limiter;

import cofm.sim.action.Action;
import cofm.sim.agent.Agent;

public interface Limiter {
	void addAgent(Agent agent);
	boolean isValidAction(Agent owner, Action action);
	void update(Agent owner, Action action);
}
