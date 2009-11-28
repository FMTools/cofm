package collab.fm.server.controller;

import java.util.List;

import org.apache.commons.beanutils.*;
import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.filter.*;
import collab.fm.server.util.Resources;
import collab.fm.server.action.Action;

public class BasicController extends Controller {
	
	static Logger logger = Logger.getLogger(BasicController.class);
	
	protected static final String[] INTERESTED_EVENTS = {
		BAD_REQUEST,
		BAD_RESPONSE,
		Resources.REQ_COMMIT,
		Resources.REQ_CONNECT,
		Resources.REQ_UPDATE,
		Resources.REQ_LOGIN,
		Resources.REQ_LOGOUT
	};
	
	public BasicController() {
		
	}
	
	@Override
	protected boolean isInterestedEvent(String name) {
		for (String s: INTERESTED_EVENTS) {
			if (s.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected List<Response> doBadRequest(Request req) {
		Action action = eventMap.get(BAD_REQUEST);
		if (action != null) {
			return action.process(req);
		}
		return null;
	}

	@Override
	protected void doBadResponse(Response rsp) {
		Action action = eventMap.get(BAD_RESPONSE);
		if (action != null) {
			action.process(rsp);
		}
	}

	@Override
	protected List<Response> doRequest(Request req) {
		try {
			Action action = eventMap.get(req.getName());
			if (action != null) {
				return action.process(req);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return doBadRequest(req);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
