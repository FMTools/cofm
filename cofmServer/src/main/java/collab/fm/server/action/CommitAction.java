package collab.fm.server.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.*;
import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.persistence.*;
import collab.fm.server.util.BeanUtils;
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
		opNameClassMap.put(Resources.OP_ADD_CHILD, BinaryRelationshipOperation.class);
		opNameClassMap.put(Resources.OP_ADD_EXCLUDE, BinaryRelationshipOperation.class);
		opNameClassMap.put(Resources.OP_ADD_REQUIRE, BinaryRelationshipOperation.class);
		
		opNameClassMap.put(Resources.OP_ADD_DES, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_ADD_NAME, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_CREATE_FEATURE, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_SET_OPT, FeatureOperation.class);
	}
	
	private Operation getOperation(Request req) {
		try {
			Operation abstractOp = BeanUtils.jsonToBean(req.getData(), Operation.class, null);
			Class<? extends Operation> concreteOpClass = opNameClassMap.get(abstractOp.getName());
			return BeanUtils.jsonToBean(req.getData(), concreteOpClass, null);
		} catch (Exception e) {
			logger.warn("Bad operation.", e);
			return null;
		}
		
	}
	
	public List<Response> process(Object input) {
		try {
			Request req = (Request)input;
			Operation op = getOperation(req);
			
			return null;
		} catch (Exception e) {
			logger.warn("Process error.", e);
			return null;
		}
		/*List<Response> result = new ArrayList<Response>(2);
		Response toRequester = new Response();
		try {
			writeSource(toRequester, (Request)input);
			
			DynaBean data = (DynaBean)((Request)input).getData();*/
			/*//String op = (String)data.get(Resources.OP_FIELD_OP);
			//Object left = data.get(Resources.OP_FIELD_LEFT);
			//Object right = data.get(Resources.OP_FIELD_RIGHT);
			//Boolean vote = (Boolean)data.get(Resources.OP_FIELD_VOTE);
			//String user = ((Request)input).getUser();
			
			//List<Operation> operations = parseOperation(op, left, right, vote, user, toRequester);
			if (operations == null) {
				result.add(toRequester);
				return result;
			}
			
			List<Operation> committedOps = new ArrayList<Operation>(2);
			for (Operation operation: operations) {
				Operation committedOp = dp.commitOperation(operation);
				if (committedOp == null) {
					logger.warn("Can't write to persistent layer: " + operation.toString()); 
					writeError(toRequester,
						MessageFormat.format(Resources.MSG_ERROR_PERSISTENT_WRITE,
								operation.toString()));
					result.add(toRequester);
					return result;
				}
				committedOps.add(committedOp);
			}
			
			for (Operation committedOp: committedOps) {
				Feature feat = dp.getFeatureById(committedOp.getLeft());
				if (feat == null) {
					logger.warn("Can't get from persistent layer: Feature(ID=" + committedOp.getLeft() + ")");
					writeError(toRequester,
							MessageFormat.format(Resources.MSG_ERROR_PERSISTENT_GET,
									"Feature(ID=" + committedOp.getLeft() + ")"));
					result.add(toRequester);
					return result;
				}
				
				if (!applyOperation(feat, committedOp, toRequester)) {
					result.add(toRequester);
					return result;
				}
				
				if (dp.updateFeature(feat)) {
					Response toOthers = new Response();
					writeSource(toOthers, (Request)input);
					broadcastOperation(toOthers, committedOp, (Request)input);
					result.add(toOthers);
				} else {
					logger.warn("Can't write to persistent layer: " + feat.toString()); 
					writeError(toRequester,
							MessageFormat.format(Resources.MSG_ERROR_PERSISTENT_WRITE,
									feat.toString().replaceAll("\n", "")));
					result.add(toRequester);
					return result;
				}
			}
			write(toRequester, Response.TYPE_BACK, Resources.RSP_SUCCESS, null);
			result.add(toRequester);
			return result;
		} catch (Exception e) {
			logger.warn("Invalid request. Maybe the 'right' operand doesn't fit the 'op'.", e);
			writeError(toRequester, Resources.MSG_ERROR_REQUEST);
			result.add(toRequester);
			return result;
		}*/
	}
	
	/*private void broadcastOperation(Response rsp, Operation op, Request req) {
		DynaClass opClass = new BasicDynaClass("OpDyna", BasicDynaBean.class, 
				new DynaProperty[] {
			new DynaProperty("op", String.class),
			new DynaProperty("left", Integer.class),
			new DynaProperty("right", isFeatureAsRightOperand(op.getName()) ? Integer.class : String.class),
			new DynaProperty("vote", Boolean.class),
			new DynaProperty("user", String.class)
		});
		DynaBean data = new BasicDynaBean(opClass);
		data.set("op", op.getName());
		data.set("left", op.getLeft());
		if (isFeatureAsRightOperand(op.getName())) {
			data.set("right", (Integer)op.getRight());
		} else {
			data.set("right", (String)op.getRight());
		}
		data.set("vote", op.getVote());
		data.set("user", req.getUser());
		write(rsp, Response.TYPE_BROADCAST, Resources.RSP_FORWARD, data);
	}
	
	private List<Operation> parseOperation(String op, Object left, Object right, Boolean vote, String user, Response response) {
		List<Operation> result = new ArrayList<Operation>(2);
		Operation o = new Operation();
		o.setName(op);
		o.setVote(vote);
		
		Integer userId = parseUserId(user, response);
		if (userId == null) {
			return null;
		}
		o.setUserid(userId);
		
		Integer featureId1 = parseFeatureId(left, response);
		if (featureId1 == null) {
			return null;
		}
		o.setLeft(featureId1);
		
		if (isFeatureAsRightOperand(op)) {
			Integer featureId2 = parseFeatureId(right, response);
			if (featureId2 == null) {
				return null;
			}
			o.setRight(featureId2);
			
			if (Resources.OP_ADD_CHILD.equals(op) && right instanceof String) {
				// add_name(featureId2, right)
				Operation extra = new Operation();
				extra.setName(Resources.OP_ADD_NAME);
				extra.setLeft(featureId2);
				extra.setRight(right);
				extra.setVote(new Boolean(true));
				extra.setUserid(o.getUserid());
				result.add(extra);
			}
		} else {
			o.setRight(right);
		}
		
		result.add(o);
		return result;
	}
	
	private Integer parseUserId(String input, Response rsp) {
		try {
		    return new Integer(input);
		} catch (NumberFormatException nfe) {
			Integer userid = dp.getUserIdByName(input);
			if (userid == null) {
				writeError(rsp, 
						MessageFormat.format(Resources.MSG_ERROR_USER_NOTFOUND, input));
				return null;
			}
			return userid;
		}
	}
	
	private Integer parseFeatureId(Object input, Response rsp) {
		if (input instanceof Integer) {
			return (Integer)input;
		} else if (input instanceof String) {
			Integer id = dp.getFeatureIdByName((String)input);
			if (id == null) {
				writeError(rsp, 
						MessageFormat.format(Resources.MSG_ERROR_FEATURE_NOTFOUND,
								input));
				return null;
			}
			return id;
		}
		return null;
	}
	
	private boolean isFeatureAsRightOperand(String op) {
		for (String name: featureAsRightOperandOp) {
			if (name.equals(op)) {
				return true;
			}
		}
		return false;
	}
*/

}
