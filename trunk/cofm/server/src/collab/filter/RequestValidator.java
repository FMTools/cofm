package collab.filter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.data.*;
import collab.filter.util.*;

public class RequestValidator extends Filter {
	
	static Logger logger = Logger.getLogger(RequestValidator.class);
	
	private static Constraint userCst = new FieldConstraint("user", String.class);
	private static Constraint dataStrCst = new FieldConstraint("data", String.class);
	private static Constraint dataIntCst = new FieldConstraint("data", Integer.class);
	private static Constraint dataOpCst = new FieldConstraint("data.op", String.class);
	private static Constraint dataLeftCst = new OrConstraint(
			new FieldConstraint("data.left", Integer.class),
			new FieldConstraint("data.left", String.class));
	private static Constraint dataRightCst = new OrConstraint(
			new FieldConstraint("data.right", Integer.class),
			new FieldConstraint("data.right", String.class));
	private static Constraint dataVoteCst = new FieldConstraint("data.vote", Boolean.class);
	
	private static ConcurrentHashMap<String, List<Constraint>> cstTable = 
		new ConcurrentHashMap<String, List<Constraint>>();
	
	static {
		cstTable.put(Resources.REQ_COMMIT, 
				Arrays.asList(userCst, dataOpCst, dataLeftCst, dataRightCst, dataVoteCst));
		cstTable.put(Resources.REQ_UPDATE,
				Arrays.asList(userCst, dataIntCst));
		cstTable.put(Resources.REQ_LISTUSER,
				Arrays.asList(userCst));
		cstTable.put(Resources.REQ_LOGOUT,
				Arrays.asList(userCst));
		cstTable.put(Resources.REQ_LOGIN,
				Arrays.asList(userCst, dataStrCst));
	}
	
	public RequestValidator(String filterName) {
		super(filterName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Request doFilterRequest(Request request) {
		try {
			List<Constraint> csts = cstTable.get(request.getName());
			for (Constraint cst: csts) {
				if (!cst.conformTo(request)) {
					onFilterError(request, Resources.REQ_ERROR_FORMAT,
							MessageFormat.format(Resources.MSG_ERROR_CONSTRAINT,
									cst.toString()));
					return null;
				}
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
		// Do nothing
		return response;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
