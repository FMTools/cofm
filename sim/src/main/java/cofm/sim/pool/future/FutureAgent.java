package cofm.sim.pool.future;

import cofm.sim.agent.*;
import cofm.sim.pool.Pool;

public class FutureAgent extends AbstractFuture {

	protected int numAgents;
	protected Agent template;
	
	// Will add "numAgents" agents according to the "template"
	public FutureAgent(Pool pool, Integer mode, Integer turn, Integer numAgents, Agent template) {
		super(pool, mode, turn);
		this.numAgents = numAgents;
		this.template = template;
	}

	@Override
	protected void makeChange() {
		int nextId = pool.numAgent() + 1;
		for (int i = 0; i < numAgents; i++) {
			CofmAgent agent = (CofmAgent) template.clone();
			agent.setId(nextId + i);
			pool.addAgent(agent);
		}
	}

}
