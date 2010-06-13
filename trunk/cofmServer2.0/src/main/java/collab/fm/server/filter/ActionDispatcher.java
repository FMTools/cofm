package collab.fm.server.filter;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
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
	protected boolean doBackwardFilter(Request req, ResponseGroup rg) {
		// Do nothing now
		return true;
	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
		throws EntityPersistenceException, InvalidOperationException {
			Action action = reqActionMap.get(req.getName());
			if (action != null) {
				logger.info("Action is about to be executing.");
				return action.execute(req, rg);
			} else {
				// No matched action, write back SUCCESS.
				rg.setBack(new Response(Resources.RSP_SUCCESS, req, null));
				return true;
			}
	}

	@Override
	protected void doDisconnection(String addr, ResponseGroup rg) {
		// TODO Auto-generated method stub
		
	}


}
