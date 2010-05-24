package collab.fm.server.action;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.EditFeatureRequest;
import collab.fm.server.bean.protocol.EditFeatureResponse;
import collab.fm.server.bean.protocol.ExitModelResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.StaleDataException;

public class SimpleBroadcastingAction extends Action {

	private static Logger logger = Logger.getLogger(SimpleBroadcastingAction.class);
	
	public SimpleBroadcastingAction() {
		super(new String[] { Resources.REQ_EDIT, Resources.REQ_EXIT_MODEL });
	}

	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		if (Resources.REQ_EDIT.equals(req.getName())) {
			EditFeatureRequest r = (EditFeatureRequest) req;
			
			EditFeatureResponse rs2 = new EditFeatureResponse();
			rs2.setFeatureId(r.getFeatureId());
			rs2.setModelId(r.getModelId());
			rs2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rs2);
		 } else if (Resources.REQ_EXIT_MODEL.equals(req.getName())) {
			ExitModelResponse emr = new ExitModelResponse();
			emr.setName(Resources.RSP_FORWARD);
			emr.setModelId(req.getModelId());
			rg.setBroadcast(emr);
		}
		
		return true;
	}

}
