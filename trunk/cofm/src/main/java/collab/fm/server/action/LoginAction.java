package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.storage.*;
import collab.fm.server.controller.*;


public class LoginAction extends Action {
	
	static Logger logger = Logger.getLogger(LoginAction.class);

	public LoginAction(Controller controller,
			DataProvider dp) {
		super(new String[]{Resources.REQ_LOGIN}, controller, dp);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public List<Response> process(Object input) {
		// TODO mock
		List<Response> result = new ArrayList<Response>();
		Response r = new Response();
		writeSource(r, (Request)input);
		write(r, Response.TYPE_BACK, Resources.RSP_SUCCESS, "login-ok");
		result.add(r);
		return result;
	}

}
