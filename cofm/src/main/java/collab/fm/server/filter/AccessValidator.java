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

	private class ClientAccessInfo {
		public int clientId;
		public Calendar lastAccessTime;
		
		public static final long MAX_INACTIVE_TIME = 1000 * 60 * 30;  // 30 minutes.
		
		public boolean isInactive() {
			Calendar now = Calendar.getInstance();
			return now.getTimeInMillis() - lastAccessTime.getTimeInMillis() > MAX_INACTIVE_TIME;
		}
	}
	// Login user ID and the client ID (client is the software while user is the human).
	private static ConcurrentHashMap<Long, ClientAccessInfo> loginClients =	new ConcurrentHashMap<Long, ClientAccessInfo>();
	
	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg) {
		if (isRestricted(req.getName())) {
			ClientAccessInfo info = loginClients.get(req.getRequesterId());
			if (info == null || info.clientId != req.getClientId()) {
				req.setLastError(Resources.MSG_ERROR_USER_DENIED);
				logger.info("Access validation failed for: " + req.getName());
				return false;
			}
			info.lastAccessTime = Calendar.getInstance();
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
				ClientAccessInfo info = loginClients.get(req.getRequesterId());
				// Prevent repeated login from different clients
				if (info != null && !info.isInactive()) {
					req.setLastError(MessageFormat.format(
							Resources.MSG_ERROR_USER_LOGIN_REPEAT, 
							((LoginRequest)req).getUser()));
					logger.info("Repeatedly login detected. (Username=" +
							((LoginRequest)req).getUser() + ")");
					return false;
				}
				if (info == null) {
					info = new ClientAccessInfo();
					info.clientId = req.getClientId();
				}
				info.lastAccessTime = Calendar.getInstance();
				loginClients.put(req.getRequesterId(), info);
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
		ClientAccessInfo info = loginClients.get(id);
		if (info != null) {
			loginClients.remove(id);
			logger.info(LogUtil.logOp(id, LogUtil.OP_LOGOUT, "" + info.clientId));
		}
		Response r = new Response();
		r.setName(Resources.RSP_FORWARD);
		r.setRequestName(Resources.REQ_LOGOUT);
		r.setRequesterId(id);
		r.setRequestClientId(info.clientId);
		rg.setBroadcast(r);
	}

	@Override
	protected void doDisconnection(Integer client, ResponseGroup rg) {
		for (Map.Entry<Long, ClientAccessInfo> entry: loginClients.entrySet()) {
			if (entry.getValue().clientId == client) {
				logger.info("Client #" + entry.getValue().clientId + " disconnected.");
				logoutUser(entry.getKey(), rg);
				return;
			}
		}
		
	}
	
}
