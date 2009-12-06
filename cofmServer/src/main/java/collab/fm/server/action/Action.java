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
	public abstract boolean execute(Request req, ResponseGroup rg) throws ActionException;
	
}
