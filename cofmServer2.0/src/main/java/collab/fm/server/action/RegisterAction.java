package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.protocol.RegisterRequest;
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


public class RegisterAction extends Action {

	static Logger logger = Logger.getLogger(RegisterAction.class);
	
	public RegisterAction() {
		super(new String[]{Resources.REQ_REGISTER});
	}

	protected boolean doExecute(Request req, ResponseGroup rg) throws EntityPersistenceException, StaleDataException {
			RegisterRequest r = (RegisterRequest) req;
			
			logger.info("Register received: <name=" + r.getUser() + ", pwd=" + r.getPwd() + ">");
			Response rsp = new Response();

			User u = DaoUtil.getUserDao().getByName(r.getUser());

			if (u == null) {
				u = new User();
				u.setName(r.getUser());
				u.setPassword(r.getPwd());
				u = DaoUtil.getUserDao().save(u);

				req.setRequesterId(u.getId());
				rsp.setMessage(Resources.MSG_REGISTER);
				rsp.setName(Resources.RSP_SUCCESS);
				
				logger.info(LogUtil.logOp(u.getId(), LogUtil.OP_REGISTER, u.getName()));
				
				Response r2 = new Response();
				r2.setMessage(r.getUser());
				r2.setName(Resources.RSP_FORWARD);
				rg.setBroadcast(r2);
				
			} else {
				rsp.setMessage(Resources.MSG_ERROR_USER_EXISTED);
				rsp.setName(Resources.RSP_ERROR);
			}

			rg.setBack(rsp);
			
			return true;
	}

}
