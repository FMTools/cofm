package collab.action;

import java.util.List;

import collab.data.*;
import collab.server.Controller;

public class FilterFailureHandler extends Action {

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
