package collab.server;

import java.util.List;

import org.apache.commons.beanutils.*;

import collab.data.*;
import collab.filter.*;
import collab.action.Action;

public class BasicController extends Controller {
	
	protected static final String[] INTERESTED_EVENTS = {
		BAD_REQUEST,
		BAD_RESPONSE,
		Resources.get(Resources.REQ_COMMIT),
		Resources.get(Resources.REQ_CONNECT),
		Resources.get(Resources.REQ_UPDATE),
		Resources.get(Resources.REQ_LOGIN),
		Resources.get(Resources.REQ_LOGOUT)
	};
	
	/**
	 * Build a filter chain consists of:
	 *  JsonConverter, AccessController
	 */
	@Override
	protected void buildFilterChain() {
		filterChain.add(new ProtocolInterpreter("json-converter"));
		filterChain.add(new RequestValidator("request-validator"));
		filterChain.add(new AccessController("access-controller"));
		filterChain.add(new ResponseValidator("response-validator"));
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

}
