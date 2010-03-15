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
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.controller.*;


public class RegisterAction extends Action {

	static Logger logger = Logger.getLogger(RegisterAction.class);
	
	public RegisterAction() {
		super(new String[]{Resources.REQ_REGISTER});
	}

	protected boolean doExecute(Request req, ResponseGroup rg) throws ActionException, StaleDataException {
		try {
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
				rsp.setRequesterId(u.getId());
				rsp.setName(Resources.RSP_SUCCESS);
			} else {
				rsp.setMessage(Resources.MSG_ERROR_USER_EXISTED);
				rsp.setName(Resources.RSP_ERROR);
			}

			rg.setBack(rsp);
			
			return true;
		} catch (BeanPersistenceException e) {
			logger.warn("Couldn't register.", e);
			throw new ActionException("Register failed.", e);
		}
	}

}
