package collab.action;

import collab.data.*;
import collab.server.Controller;

public abstract class Action {
	
	public Action(String[] interestedEvents, Controller controller) {
		for (String event: interestedEvents) {
			controller.registerAction(event, this);
		}
	}
	
	public abstract Response process(Object input);
}
