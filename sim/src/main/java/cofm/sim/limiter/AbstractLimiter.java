package cofm.sim.limiter;

import java.util.HashMap;
import java.util.Map;

import cofm.sim.action.Action;
import cofm.sim.agent.Agent;
import cofm.sim.limiter.Limiter.LimiterInfo;
import cofm.sim.pool.Pool;

public abstract class AbstractLimiter implements Limiter {

	protected Pool pool;
	protected Map<Agent, LimiterInfo> info = new HashMap<Agent, LimiterInfo>();
	
	public AbstractLimiter(Pool pool) {
		this.pool = pool;
		pool.setLimiter(this);
	}
	
	public void addAgent(Agent agent) {
		info.put(agent, initLimiterInfo(agent));
	}
	
	public LimiterInfo getAgentInfo(Agent agent) {
		return info.get(agent);
	}
	
	abstract protected LimiterInfo initLimiterInfo(Agent agent);

}
