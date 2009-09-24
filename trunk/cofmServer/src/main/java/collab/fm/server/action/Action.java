package collab.fm.server.action;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.storage.*;
import collab.fm.server.controller.*;

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
	protected abstract Logger getLogger();
	
	protected void write(Response rsp, String type, String status, Object data) {
		rsp.setType(type);
		Response.Body body = (Response.Body)rsp.getBody();
		body.setStatus(status);
		body.setData(data);
	}
	
	protected void writeError(Response rsp, String message) {
		write(rsp, Response.TYPE_BACK, Resources.RSP_ERROR, message);
	}
	
	protected void writeSource(Response rsp, Request req) {
		Response.Body.Source src = ((Response.Body)rsp.getBody()).getSource();
		src.setAddress(req.getAddress());
		src.setId(req.getId());
		src.setName(req.getName());
		src.setUser(req.getUser());
	}
}
