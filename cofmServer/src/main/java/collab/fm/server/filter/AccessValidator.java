package collab.fm.server.filter;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
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
		Resources.REQ_LISTUSER,
		Resources.REQ_LOGOUT
	};
	
	private ConcurrentHashMap<String, String> loginUsers = 
		new ConcurrentHashMap<String, String>();
	
	

	@Override
	protected void doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected FilterException onFilterError(Request req, ResponseGroup rg,
			Throwable t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*@Override
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
	}*/

	private boolean isRestricted(String name) {
		for (int i = 0; i < restricted.length; i++) {
			if (restricted[i].equals(name)) {
				return true;
			}
		}
		return false;
	}
	
}
