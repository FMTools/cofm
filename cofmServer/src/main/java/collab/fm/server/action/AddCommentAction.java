package collab.fm.server.action;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.StaleDataException;

public class AddCommentAction extends Action {

	public AddCommentAction() {
		super(new String[] { Resources.REQ_COMMENT });
	}

	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		// TODO Auto-generated method stub
		return true;
	}

}
