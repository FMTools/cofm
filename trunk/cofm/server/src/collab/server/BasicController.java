package collab.server;

import collab.data.*;
import collab.filter.*;

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
	protected void initEvents() {
		for (String name: INTERESTED_EVENTS) {
			eventMap.put(name, null);
		}
	}
	
	@Override
	protected Response doBadRequest(Request req) {
		
		return null;
	}

	@Override
	protected Response doBadResponse(Response rsp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response doRequest(Request req) {
		// TODO Auto-generated method stub
		return null;
	}

}
