package collab.filter;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.*;

import collab.data.*;

public class AccessController extends Filter {
	
	private static final String[] restricted = {
		Resources.get(Resources.REQ_COMMIT),
		Resources.get(Resources.REQ_UPDATE),
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
			DynaBean body = (DynaBean)request.body();
			String name = (String)body.get(Resources.get(Resources.REQ_FIELD_NAME));
			String user = (String)body.get(Resources.get(Resources.REQ_FIELD_USER));
			if (isRestricted(name)) {
				String address = loginUsers.get(user);
				if (address == null || !address.equals(request.address().toString())) {
					onFilterError(request, Resources.get(Resources.REQ_ERROR_AUTHORITY),
							Resources.get(Resources.MSG_ERROR_DENIED));
					return null;
				}
			} else if (Resources.get(Resources.REQ_LOGOUT).equals(name)) {
				loginUsers.remove(user);
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
			DynaBean body = (DynaBean)response.body();
			String name = (String)body.get(Resources.get(Resources.RSP_FIELD_NAME));
			String sourceName = (String)body.get(Resources.get(Resources.RSP_FIELD_SOURCE_NAME));
			if (Resources.get(Resources.RSP_SUCCESS).equals(name) &&
					Resources.get(Resources.REQ_LOGIN).equals(sourceName)) {
				// successful login
				String sourceUser = (String)body.get(Resources.get(Resources.RSP_FIELD_SOURCE_USER));
				String sourceAddr = (String)body.get(Resources.get(Resources.RSP_FIELD_SOURCE_ADDR));
				loginUsers.put(sourceUser, sourceAddr);
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
