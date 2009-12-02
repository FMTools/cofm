package collab.fm.server.filter;

import java.util.HashMap;
import java.util.Map;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.FilterException;
import collab.fm.server.action.Action;

public class ActionDispatcher extends Filter {

	private Map<String, Action> reqActionMap = new HashMap<String, Action>();
	
	public void registerAction(String[] requestNames, Action action) {
		for (String name: requestNames) {
		    reqActionMap.put(name, action);
		}
	}
	
	public ActionDispatcher() {
		
	}
	
	@Override
	protected void doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub

	}

	@Override
	protected FilterException onFilterError(Request req, ResponseGroup rg,
			Throwable t) {
		// TODO Auto-generated method stub
		return null;
	}

}
