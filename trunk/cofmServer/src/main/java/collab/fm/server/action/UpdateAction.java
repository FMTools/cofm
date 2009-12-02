package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.persistence.*;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.controller.*;


public class UpdateAction extends Action {
	
	static Logger logger = Logger.getLogger(UpdateAction.class);
	
	public UpdateAction() {
		super(new String[]{Resources.REQ_UPDATE});
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(Request req, ResponseGroup rg) throws ActionException {
		// TODO Auto-generated method stub
		
	}

}
