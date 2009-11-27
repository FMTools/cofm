package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.json.Request;
import collab.fm.server.bean.json.Response;
import collab.fm.server.persistence.*;
import collab.fm.server.util.Resources;
import collab.fm.server.controller.*;


public class RegisterAction extends Action {

	static Logger logger = Logger.getLogger(RegisterAction.class);
	
	public RegisterAction(Controller controller) {
		super(new String[]{Resources.REQ_REGISTER}, controller);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Response> process(Object input) {
		// TODO mock
		List<Response> result = new ArrayList<Response>();
		
		User user = new User();
		user.setName(((Request)input).getUser());
		//user = dp.addUser(user);
		
		Response r = makeBackResponse((Request)input,Resources.RSP_SUCCESS, user);
		result.add(r);
		return result;
	}

}
