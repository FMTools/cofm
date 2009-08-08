package collab.action;

import java.util.List;

import collab.data.*;
import collab.storage.DataProvider;
import collab.server.Controller;

public abstract class Action {
	
	protected DataProvider dp;
	public Action(String[] interestedEvents, Controller controller, DataProvider dp) {
		for (String event: interestedEvents) {
			controller.registerAction(event, this);
		}
		this.dp = dp;
	}
	
	/**
	 * Most action should return a "write back" response to notify the requester about
	 * the result of process. 
	 * @param input
	 * @return responses
	 */
	public abstract List<Response> process(Object input);
}
