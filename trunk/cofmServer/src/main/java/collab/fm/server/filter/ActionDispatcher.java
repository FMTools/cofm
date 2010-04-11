package collab.fm.server.filter;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.FilterException;
import collab.fm.server.action.Action;

public class ActionDispatcher extends Filter {

	static Logger logger = Logger.getLogger(ActionDispatcher.class);
	
	private Map<String, Action> reqActionMap = new HashMap<String, Action>();
	
	public void registerAction(String[] requestNames, Action action) {
		for (String name: requestNames) {
		    reqActionMap.put(name, action);
		}
	}
	
	public ActionDispatcher() {
		
	}
	
	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// Do nothing now
		return true;
	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		try {
			Action action = reqActionMap.get(req.getName());
			if (action != null) {
				logger.info("Action is about to be executing.");
				return action.execute(req, rg);
			}
			return true; 
		} catch (ActionException ae) {
			req.setLastError("ActionException: " + ae.getMessage());
			logger.error("Action failed.", ae);
			throw new FilterException(ae);
		}
	}

	@Override
	public void onClientDisconnected(String address) {
		// TODO Auto-generated method stub
		
	}

}
