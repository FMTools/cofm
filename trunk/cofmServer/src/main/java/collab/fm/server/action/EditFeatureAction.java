package collab.fm.server.action;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.EditFeatureRequest;
import collab.fm.server.bean.protocol.EditFeatureResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.StaleDataException;

public class EditFeatureAction extends Action {

	private static Logger logger = Logger.getLogger(EditFeatureAction.class);
	
	public EditFeatureAction() {
		super(new String[] { Resources.REQ_EDIT });
	}

	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		EditFeatureRequest r = (EditFeatureRequest) req;
		
		EditFeatureResponse rs2 = new EditFeatureResponse();
		rs2.setFeatureId(r.getFeatureId());
		rs2.setModelId(r.getModelId());
		rs2.setName(Resources.RSP_FORWARD);
		rg.setBroadcast(rs2);
		
		return true;
	}

}
