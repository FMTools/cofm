package collab.fm.server.action;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.persistence.*;
import collab.fm.server.controller.*;


public class FilterFailureHandler extends Action {

	static Logger logger = Logger.getLogger(FilterFailureHandler.class);
	
	public FilterFailureHandler(Controller controller) {
		super(new String[] {
			Controller.BAD_REQUEST,
			Controller.BAD_RESPONSE
		}, controller);
	}

	@Override
	public List<Response> process(Object input) {
		if (input instanceof Request) {
			return doRequest((Request)input);
		} else if (input instanceof Response) {
			doResponse((Response)input);
			return null;
		}
		return null;
	}
	
	protected List<Response> doRequest(Request req) {
		return null;
	}
	
	protected void doResponse(Response rsp) {
	}

}
