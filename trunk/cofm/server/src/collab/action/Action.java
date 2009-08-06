package collab.action;

import java.util.List;

import collab.data.*;
import collab.server.Controller;

public abstract class Action {
	
	public Action(String[] interestedEvents, Controller controller) {
		for (String event: interestedEvents) {
			controller.registerAction(event, this);
		}
	}
	
	/**
	 * Most action should return a "write back" response to notify the requester about
	 * the result of process. 
	 * @param input
	 * @return responses
	 */
	public abstract List<Response> process(Object input);
}
