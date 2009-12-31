package collab.fm.server.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.operation.BinaryRelationshipOperation;
import collab.fm.server.bean.operation.FeatureOperation;
import collab.fm.server.bean.operation.Operation;
import collab.fm.server.bean.protocol.CommitRequest;
import collab.fm.server.bean.protocol.CommitResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.StaleDataException;
import collab.fm.server.controller.*;


public class CommitAction extends Action {

	static Logger logger = Logger.getLogger(CommitAction.class);
	
	public CommitAction() {
		super(new String[] { Resources.REQ_COMMIT });
	}

	protected boolean doExecute(Request req, ResponseGroup rg) throws ActionException, StaleDataException {
		try {
			List<Operation> ops = ((CommitRequest)req).getOperation().apply();
			
			// Create the association between this user and current model.
			User me = DaoUtil.getUserDao().getById(req.getRequesterId(), false);
			Model currentModel = DaoUtil.getModelDao().getById(req.getModelId(), false);
			if (me != null && currentModel != null) {
				me.addModel(currentModel);
				DaoUtil.getUserDao().save(me);
			}
			
			CommitResponse cr = new CommitResponse();
			cr.setName(Resources.RSP_SUCCESS);
			cr.setOperations(ops);
			
			CommitResponse cr2 = new CommitResponse();
			cr2.setName(Resources.RSP_FORWARD);
			cr2.setOperations(ops);
			
			rg.setBack(cr);
			rg.setBroadcast(cr2);
			
			return true;
		} catch (StaleDataException sde) {
			logger.info("Stale data found.");
			throw sde;
		} catch (Exception e) {
			logger.warn("Execution failed.", e);
			throw new ActionException("Action execution failed."); 
		}
	}

}
