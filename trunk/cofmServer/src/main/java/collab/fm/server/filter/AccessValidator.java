package collab.fm.server.filter;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.FilterException;

public class AccessValidator extends Filter {
	
	static Logger logger = Logger.getLogger(AccessValidator.class);
	
	private static final String[] restricted = {
		Resources.REQ_COMMIT,
		Resources.REQ_UPDATE,
		Resources.REQ_LOGOUT,
		Resources.REQ_CREATE_MODEL
	};
	
	private static ConcurrentHashMap<Long, String> loginUsers = new ConcurrentHashMap<Long, String>();
	
	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		try {
			if (isRestricted(req.getName())) {
				String address = loginUsers.get(req.getRequesterId());
				if (address == null || !address.equals(req.getAddress())) {
					req.setLastError(Resources.MSG_ERROR_USER_DENIED);
					logger.info("Access validation failed for: " + req.getName());
					return false;
				}
			}
			if (Resources.REQ_LOGOUT.equals(req.getName())) {
				loginUsers.remove(req.getRequesterId());
			}
			logger.info("Access validation OK for: " + req.getName());
			return true;
		} catch (Exception e) {
			logger.error("Exception caught.", e);
			throw new FilterException(e);
		}	
	}
	
	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		try {
			Response rsp = rg.getBack();
			if (rsp == null) {
				return true;
			}
			if (Resources.RSP_SUCCESS.equals(rsp.getName()) &&
					Resources.REQ_LOGIN.equals(req.getName())) {
				// successful login
				loginUsers.put(req.getRequesterId(), req.getAddress());
				logger.info("User login succeed.");
			}
			return true;
		} catch (Exception e) {
			logger.error("Exception caught.", e);
			throw new FilterException(e);
		}
	}


	private boolean isRestricted(String name) {
		for (int i = 0; i < restricted.length; i++) {
			if (restricted[i].equals(name)) {
				return true;
			}
		}
		return false;
	}
	
}
