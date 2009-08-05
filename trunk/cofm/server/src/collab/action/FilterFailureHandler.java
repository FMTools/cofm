package collab.action;

import collab.data.*;
import collab.server.Controller;

public class FilterFailureHandler extends Action {

	public FilterFailureHandler(Controller controller) {
		super(new String[] {
			Controller.BAD_REQUEST,
			Controller.BAD_RESPONSE
		}, controller);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response process(Object input) {
		return null;
	}
}
