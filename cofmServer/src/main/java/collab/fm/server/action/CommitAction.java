package collab.fm.server.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.BinaryRelationshipOperation;
import collab.fm.server.bean.protocol.FeatureOperation;
import collab.fm.server.bean.protocol.Operation;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.controller.*;


public class CommitAction extends Action {
	
	public CommitAction(Controller controller) {
		super(new String[] { Resources.REQ_COMMIT }, controller);
	}

	static Logger logger = Logger.getLogger(CommitAction.class);
	
	private static Map<String, Class<? extends Operation>> opNameClassMap = 
		new HashMap<String, Class<? extends Operation>>();
	static {
		opNameClassMap.put(Resources.OP_CREATE_BINARY_RELATIONSHIP, BinaryRelationshipOperation.class);
		
		opNameClassMap.put(Resources.OP_ADD_DES, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_ADD_NAME, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_CREATE_FEATURE, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_SET_OPT, FeatureOperation.class);
	}
	
	private Operation getOperation(Request req) {
		try {
			Operation abstractOp = BeanUtil.jsonToBean(req.getData(), Operation.class, null);
			Class<? extends Operation> concreteOpClass = opNameClassMap.get(abstractOp.getName());
			return BeanUtil.jsonToBean(req.getData(), concreteOpClass, null);
		} catch (Exception e) {
			logger.warn("Bad operation.", e);
			return null;
		}
		
	}
	
	public List<Response> process(Object input) {
		List<Response> result = new ArrayList<Response>();
		try {
			Request req = (Request)input;
			
			// apply the operation 
			Operation op = getOperation(req);
			Operation appliedOp = op.apply();
			
			Response rspBack = makeBackResponse(req, Resources.RSP_SUCCESS, appliedOp);
			Response rspBroadcast = makeBroadcastResponse(req, appliedOp);
			result.add(rspBack);
			result.add(rspBroadcast);
		} catch (Exception e) {
			logger.warn("Process error.", e);
			Response rspError = makeErrorResponse((Request)input, "CommitAction throws an exception: " + e.toString());
			result.add(rspError);
		}
		return result;
	}

}
