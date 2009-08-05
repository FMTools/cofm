package collab.server;

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
		filterChain.add(new JsonConverter("json-converter"));
		filterChain.add(new AccessController("access-controller"));
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
	protected Response doBadRequest(Request req) {
		Action action = eventMap.get(BAD_REQUEST);
		if (action != null) {
			return action.process(req);
		}
		return null;
	}

	@Override
	protected Response doBadResponse(Response rsp) {
		Action action = eventMap.get(BAD_RESPONSE);
		if (action != null) {
			return action.process(rsp);
		}
		return null;
	}

	@Override
	protected Response doRequest(Request req) {
		try {
			DynaBean bean = (DynaBean)req.body();
			Action action = eventMap.get(bean.get(Resources.get(Resources.REQ_FIELD_NAME)));
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
