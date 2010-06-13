package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.protocol.LoginRequest;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.persistence.*;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.LogUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.controller.*;


public class LoginAction extends Action {

	static Logger logger = Logger.getLogger(LoginAction.class);
	
	public LoginAction() {
		super(new String[] { Resources.REQ_LOGIN });
	}

	protected boolean doExecute(Request req, ResponseGroup rg) throws EntityPersistenceException, StaleDataException {
			LoginRequest lr = (LoginRequest)req;
			Response rsp = new Response();
			
			User example = new User();
			example.setName(lr.getUser());
			example.setPassword(lr.getPwd());
		
			User user = DaoUtil.getUserDao().checkThenGet(example);
			
			if (user == null) {
				rsp.setMessage(Resources.MSG_ERROR_USER_LOGIN_FAILED);
				rsp.setName(Resources.RSP_ERROR);
			} else {
				rsp.setRequesterId(user.getId());
				req.setRequesterId(user.getId());
				rsp.setMessage(Resources.MSG_LOGIN);
				rsp.setName(Resources.RSP_SUCCESS);
				
				logger.info(LogUtil.logOp(user.getId(), LogUtil.OP_LOGIN,
						user.getName() + " " + req.getAddress()));				
			}
			
			rg.setBack(rsp);
			
			return true;
	}
	
}
