package collab.fm.server.filter;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.LoginRequest;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.LogUtil;
import collab.fm.server.util.Resources;

public class AccessValidator extends Filter {
	
	static Logger logger = Logger.getLogger(AccessValidator.class);
	
	private static final String[] restricted = {
		Resources.REQ_UPDATE,
		Resources.REQ_LOGOUT,
		Resources.REQ_CREATE_MODEL
	};

	// TODO: show message in client
	private static ConcurrentHashMap<Long, String> loginUserAddrs =	new ConcurrentHashMap<Long, String>();
	
	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg) {
		if (isRestricted(req.getName())) {
				String address = loginUserAddrs.get(req.getRequesterId());
				if (address == null || !address.equals(req.getAddress())) {
					req.setLastError(Resources.MSG_ERROR_USER_DENIED);
					logger.info("Access validation failed for: " + req.getName());
					return false;
				}
			}
			if (Resources.REQ_LOGOUT.equals(req.getName())) {
				logoutUser(req.getRequesterId(), rg);
			}
			logger.info("Access validation OK for: " + req.getName());
			return true;
	}
	
	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg) {
			Response rsp = rg.getBack();
			if (rsp == null) {
				return true;
			}
			if (Resources.RSP_SUCCESS.equals(rsp.getName()) &&
					Resources.REQ_LOGIN.equals(req.getName())) {
				// Prevent repeated login
				if (loginUserAddrs.get(req.getRequesterId()) != null) {
					req.setLastError(MessageFormat.format(
							Resources.MSG_ERROR_USER_LOGIN_REPEAT, 
							((LoginRequest)req).getUser()));
					logger.info("Repeatedly login detected. (Username=" +
							((LoginRequest)req).getUser() + ")");
					return false;
				}
				loginUserAddrs.put(req.getRequesterId(), req.getAddress());
				logger.info("User login succeed.");
			}
			return true;
	}
	

	private boolean isRestricted(String name) {
		for (int i = 0; i < restricted.length; i++) {
			if (restricted[i].equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private void logoutUser(Long id, ResponseGroup rg) {
		String addr = loginUserAddrs.get(id);
		if (addr != null) {
			loginUserAddrs.remove(id);
			logger.info(LogUtil.logOp(id, LogUtil.OP_LOGOUT, addr));
		}
		Response r = new Response();
		r.setName(Resources.RSP_FORWARD);
		r.setRequestName(Resources.REQ_LOGOUT);
		r.setRequesterId(id);
		rg.setBroadcast(r);
	}

	@Override
	protected void doDisconnection(String addr, ResponseGroup rg) {
		for (Map.Entry<Long, String> entry: loginUserAddrs.entrySet()) {
			if (entry.getValue().equals(addr)) {
				logger.info("Client <" + entry.getKey() + ", " + entry.getValue() + "> disconnected.");
				logoutUser(entry.getKey(), rg);
				return;
			}
		}
		
	}
	
}
