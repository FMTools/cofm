package collab.fm.server.filter;

import java.text.MessageFormat;
import java.util.Calendar;
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

	// Login user ID and the client ID (client is the browser software).
	private static ConcurrentHashMap<Long, Integer> loginClients =	new ConcurrentHashMap<Long, Integer>();
	
	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg) {
		if (isRestricted(req.getName())) {
			Integer client = loginClients.get(req.getRequesterId());
			if (client == null || !client.equals(req.getClientId())) {
				req.setLastError(Resources.MSG_ERROR_USER_DENIED);
				logger.info("Access validation failed for: " + req.getName());
				return false;
			}
		}
		// Handle log out
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
				Integer client = loginClients.get(req.getRequesterId());
				// Prevent repeated login from different clients
				if (client != null && !client.equals(req.getClientId())) {
					req.setLastError(MessageFormat.format(
							Resources.MSG_ERROR_USER_LOGIN_REPEAT, 
							((LoginRequest)req).getUser()));
					logger.info("Repeatedly login detected. (Username=" +
							((LoginRequest)req).getUser() + ")");
					return false;
				}
				loginClients.put(req.getRequesterId(), req.getClientId());
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
		Integer client = loginClients.get(id);
		if (client != null) {
			loginClients.remove(id);
			logger.info(LogUtil.logOp(id, LogUtil.OP_LOGOUT, "" + client));
		}
		Response r = new Response();
		r.setName(Resources.RSP_FORWARD);
		r.setRequestName(Resources.REQ_LOGOUT);
		r.setRequesterId(id);
		r.setRequestClientId(client);
		rg.setBroadcast(r);
	}

	@Override
	protected void doDisconnection(Integer client, ResponseGroup rg) {
		for (Map.Entry<Long, Integer> entry: loginClients.entrySet()) {
			if (entry.getValue().equals(client)) {
				logger.info("Client #" + entry.getValue() + " disconnected.");
				logoutUser(entry.getKey(), rg);
				return;
			}
		}
		
	}
	
}
