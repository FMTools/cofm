package checker;

import model.Feature;

/**
 * No real checking here. Just for experiments.
 *
 */
public class PreloadedDeadStatePolicy implements DeadCheckPolicy {

	public String toString() {
		return "Not a real checker.";
	}
	
	@Override
	public boolean isDead(Feature feature) {
		return feature.getDead() == Feature.DEAD;
	}

}
