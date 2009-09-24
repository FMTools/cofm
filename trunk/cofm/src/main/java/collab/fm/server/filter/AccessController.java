package collab.fm.server.filter;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;

public class AccessController extends Filter {
	
	static Logger logger = Logger.getLogger(AccessController.class);
	
	private static final String[] restricted = {
		Resources.REQ_COMMIT,
		Resources.REQ_UPDATE,
		Resources.REQ_LISTUSER,
		Resources.REQ_LOGOUT
	};
	
	private ConcurrentHashMap<String, String> loginUsers = 
		new ConcurrentHashMap<String, String>();
	
	public AccessController(String filterName) {
		super(filterName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Request doFilterRequest(Request request) {
		try {
			if (isRestricted(request.getName())) {
				String address = loginUsers.get(request.getUser());
				if (address == null || !address.equals(request.getAddress())) {
					onFilterError(request, Resources.REQ_ERROR_AUTHORITY,
							Resources.MSG_ERROR_USER_DENIED);
					return null;
				}
			}
			if (Resources.REQ_LOGOUT.equals(request.getName())) {
				loginUsers.remove(request.getUser());
			}
			return request;
		} catch (Exception e) {
			onFilterError(request, Resources.REQ_ERROR_FORMAT,
					MessageFormat.format(Resources.MSG_ERROR_EXCEPTION, e.getMessage()), e);
			return null;
		}
	}

	@Override
	protected Response doFilterResponse(Response response) {
		try {
			Response.Body body = (Response.Body)response.getBody();
			if (Resources.RSP_SUCCESS.equals(body.getStatus()) &&
					Resources.REQ_LOGIN.equals(body.getSource().getName())) {
				// successful login
				loginUsers.put(body.getSource().getUser(), body.getSource().getAddress());
			}
			return response;
		} catch (Exception e) {
			onFilterError(response, Resources.RSP_ERROR_FORMAT,
					MessageFormat.format(Resources.MSG_ERROR_EXCEPTION, e.getMessage()), e);
			return null;
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

	@Override
	protected Logger getLogger() {
		return logger;
	}
	
}
