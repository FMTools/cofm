package collab.fm.server.action;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.persistence.*;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.controller.*;

public abstract class Action {
	
	
	public Action(String[] interestedEvents) {
		Controller.instance().registerAction(interestedEvents, this);
	}
	
	/**
	 * Business logic executed here. <br/>
	 * NOTE: Don't need to report error in Actions.
	 * @param req
	 * @param rg
	 * @return TODO
	 * @throws ActionException
	 */
	protected abstract boolean doExecute(Request req, ResponseGroup rg) throws ActionException, StaleDataException;
	
	public boolean execute(Request req, ResponseGroup rg) throws ActionException {
		try {
			return doExecute(req, rg);
		} catch (StaleDataException sde) {
			reportStaleData(req, rg);
			return true;
		} catch (ActionException ae) {
			throw ae;
		}
	}
	
	protected void reportStaleData(Request req, ResponseGroup rg) {
		Response r = new Response();
		r.setName(Resources.RSP_STALE);
		
		rg.setBack(r);
		rg.setBroadcast(null);
		rg.setPeer(null);
	}
	
}
