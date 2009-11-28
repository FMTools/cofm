package collab.fm.server.action;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.persistence.*;
import collab.fm.server.util.Resources;
import collab.fm.server.controller.*;

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
	
	protected Response makeResponse(Request req, String type, String status, Object data) {
		Response rsp = new Response();
		writeSource(rsp, req);
		rsp.setType(type);
		Response.Body body = (Response.Body)rsp.getBody();
		body.setStatus(status);
		body.setData(data);
		return rsp;
	}
	
	protected Response makePeerResponse(Request req, Object data, List<String> targets) {
		Response rsp = makeResponse(req, Response.TYPE_PEER_FORWARD, Resources.RSP_FORWARD, data);
		rsp.setTargets(targets);
		return rsp;
	}
	
	protected Response makeBroadcastResponse(Request req, Object data) {
		return makeResponse(req, Response.TYPE_BROADCAST_FORWARD, Resources.RSP_FORWARD, data);
	}
	
	protected Response makeBackResponse(Request req, String status, Object data) {
		return makeResponse(req, Response.TYPE_BACK, status, data);
	}
	
	protected Response makeErrorResponse(Request req, String message) {
		return makeBackResponse(req, Resources.RSP_ERROR, message);
	}
	
	private void writeSource(Response rsp, Request req) {
		Response.Body.Source src = ((Response.Body)rsp.getBody()).getSource();
		src.setAddress(req.getAddress());
		src.setId(req.getId());
		src.setName(req.getName());
		src.setUser(req.getUser());
	}
}
