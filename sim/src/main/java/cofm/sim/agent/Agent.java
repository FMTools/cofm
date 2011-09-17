package cofm.sim.agent;

public interface Agent extends Cloneable {
	void executeAction();

	Agent clone();
}
