package collab.filter;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import collab.data.*;

public class AccessController extends Filter {
	
	private static final String[] restricted = {
		Resources.get(Resources.REQ_COMMIT),
		Resources.get(Resources.REQ_UPDATE),
		Resources.get(Resources.REQ_LISTUSER),
		Resources.get(Resources.REQ_LOGOUT)
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
					onFilterError(request, Resources.get(Resources.REQ_ERROR_AUTHORITY),
							Resources.get(Resources.MSG_ERROR_USER_DENIED));
					return null;
				}
			}
			if (Resources.get(Resources.REQ_LOGOUT).equals(request.getName())) {
				loginUsers.remove(request.getUser());
			}
			return request;
		} catch (Exception e) {
			onFilterError(request, Resources.get(Resources.REQ_ERROR_FORMAT),
					MessageFormat.format(Resources.get(Resources.MSG_ERROR_EXCEPTION), e.getMessage()));
			return null;
		}
	}

	@Override
	protected Response doFilterResponse(Response response) {
		try {
			Response.Body body = (Response.Body)response.getBody();
			if (Resources.get(Resources.RSP_SUCCESS).equals(body.getStatus()) &&
					Resources.get(Resources.REQ_LOGIN).equals(body.getSource().getName())) {
				// successful login
				loginUsers.put(body.getSource().getUser(), body.getSource().getAddress());
			}
			return response;
		} catch (Exception e) {
			onFilterError(response, Resources.get(Resources.RSP_ERROR_FORMAT),
					MessageFormat.format(Resources.get(Resources.MSG_ERROR_EXCEPTION), e.getMessage()));
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
	
}
