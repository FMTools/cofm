package collab.action;

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
	public Response process(Object input) {
		if (input instanceof Request) {
			return doRequest((Request)input);
		} else if (input instanceof Response) {
			return doResponse((Response)input);
		}
		return null;
	}
	
	protected Response doRequest(Request req) {
		return null;
	}
	
	protected Response doResponse(Response rsp) {
		return null;
	}
}
