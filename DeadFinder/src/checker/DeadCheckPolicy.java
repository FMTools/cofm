package checker;

import model.Feature;

public interface DeadCheckPolicy {

	boolean isDead(Feature feature);
}
