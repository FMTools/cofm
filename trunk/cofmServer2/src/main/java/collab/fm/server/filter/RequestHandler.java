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
import collab.fm.server.util.exception.StaleDataException;

public class RequestHandler extends Filter {

	static Logger logger = Logger.getLogger(RequestHandler.class);
	
	public RequestHandler() {
		
	}
	
	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg) {
		// Do nothing now
		return true;
	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
		throws EntityPersistenceException, InvalidOperationException {
		try {
			return req.process(rg);
		} catch (StaleDataException e) {
			// Report stale data (we must return true here.)
			Response rsp = new Response(req);
			rsp.setName(Resources.RSP_STALE);
			return true;
		}
		
	}

	@Override
	protected void doDisconnection(String addr, ResponseGroup rg) {
		// TODO Auto-generated method stub
		
	}


}
