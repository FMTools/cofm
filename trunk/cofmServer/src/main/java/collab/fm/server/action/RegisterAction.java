package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.persistence.*;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.controller.*;


public class RegisterAction extends Action {

	static Logger logger = Logger.getLogger(RegisterAction.class);
	
	public RegisterAction() {
		super(new String[]{Resources.REQ_REGISTER});
	}

	protected boolean doExecute(Request req, ResponseGroup rg) throws ActionException {
		return false;
		// TODO Auto-generated method stub
		
	}



}
