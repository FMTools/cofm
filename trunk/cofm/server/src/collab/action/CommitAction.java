package collab.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.*;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.*;
import collab.server.Controller;
import collab.storage.DataProvider;

public class CommitAction extends Action {
	
	static Logger logger = Logger.getLogger(CommitAction.class);
	
	private final class OpApplyPolicy {
		private Class<?> operandType;
		private Method callee;
		
		public OpApplyPolicy(Class<?> operandType, Method callee) {
			this.operandType = operandType;
			this.callee = callee;
		}
		
		public void apply(Feature feat, Operation op) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			if (operandType == null) {
				callee.invoke(feat, op.getVote(), op.getUserid());
			} else {
				callee.invoke(feat, operandType.cast(op.getRight()), op.getVote(), op.getUserid());
			}
		}
	}
	
	private ConcurrentHashMap<String, OpApplyPolicy> opApplyPolicies = 
		new ConcurrentHashMap<String, OpApplyPolicy>();
	

	
	private static final String[] featureAsRightOperandOp = {
		Resources.OP_ADDCHILD,
		Resources.OP_ADDEXCLUDE,
		Resources.OP_ADDREQUIRE
	};

	public CommitAction(Controller controller,
			DataProvider dp) {
		super(new String[]{Resources.REQ_COMMIT}, controller, dp);
		initOpApplyPolicies();
	}
	
	private void initOpApplyPolicies() {
		try {
			opApplyPolicies.put(Resources.OP_ADDCHILD,
					new OpApplyPolicy(Integer.class, 
							Feature.class.getMethod("voteChild", Integer.class, Boolean.class, Integer.class)));
			opApplyPolicies.put(Resources.OP_ADDDES,
					new OpApplyPolicy(String.class, 
							Feature.class.getMethod("voteDescription", String.class, Boolean.class, Integer.class)));
			opApplyPolicies.put(Resources.OP_ADDEXCLUDE,
					new OpApplyPolicy(Integer.class, 
							Feature.class.getMethod("voteExcluding", Integer.class, Boolean.class, Integer.class)));
			opApplyPolicies.put(Resources.OP_ADDNAME,
					new OpApplyPolicy(String.class, 
							Feature.class.getMethod("voteName", String.class, Boolean.class, Integer.class)));
			opApplyPolicies.put(Resources.OP_ADDREQUIRE,
					new OpApplyPolicy(Integer.class, 
							Feature.class.getMethod("voteRequiring", Integer.class, Boolean.class, Integer.class)));
			opApplyPolicies.put(Resources.OP_SETEXT, 
					new OpApplyPolicy(null, 
							Feature.class.getMethod("voteFeature", Boolean.class, Integer.class)));
			opApplyPolicies.put(Resources.OP_SETOPT, 
					new OpApplyPolicy(null, 
							Feature.class.getMethod("voteMandatory", Boolean.class, Integer.class)));
		} catch (NoSuchMethodException e) {
			logger.error("OpApplyPolicies init failed.", e);
		}
	}

	@Override
	public List<Response> process(Object input) {
		List<Response> result = new ArrayList<Response>(2);
		Response toRequester = new Response();
		try {
			writeSource(toRequester, (Request)input);
			
			DynaBean data = (DynaBean)((Request)input).getData();
			String op = (String)data.get(Resources.OP_FIELD_OP);
			Object left = data.get(Resources.OP_FIELD_LEFT);
			Object right = data.get(Resources.OP_FIELD_RIGHT);
			Boolean vote = (Boolean)data.get(Resources.OP_FIELD_VOTE);
			String user = ((Request)input).getUser();
			
			List<Operation> operations = makeOperation(op, left, right, vote, user, toRequester);
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
		}
	}
	
	private boolean applyOperation(Feature feat, Operation op, Response rsp) {
		try {
			opApplyPolicies.get(op.getOp()).apply(feat, op);
			return true;
		} catch (Exception e) {
			logger.warn("Can't apply " + op.toString() + " on " + feat.toString(), e);
			writeError(rsp, MessageFormat.format(Resources.MSG_ERROR_FEATURE_APPLYOP, 
					op.toString(), feat.toString().replaceAll("\n", "")));
			return false;
		}
	}
	
	private void broadcastOperation(Response rsp, Operation op, Request req) {
		DynaClass opClass = new BasicDynaClass("OpDyna", BasicDynaBean.class, 
				new DynaProperty[] {
			new DynaProperty("op", String.class),
			new DynaProperty("left", Integer.class),
			new DynaProperty("right", isFeatureAsRightOperand(op.getOp()) ? Integer.class : String.class),
			new DynaProperty("vote", Boolean.class),
			new DynaProperty("user", String.class)
		});
		DynaBean data = new BasicDynaBean(opClass);
		data.set("op", op.getOp());
		data.set("left", op.getLeft());
		if (isFeatureAsRightOperand(op.getOp())) {
			data.set("right", (Integer)op.getRight());
		} else {
			data.set("right", (String)op.getRight());
		}
		data.set("vote", op.getVote());
		data.set("user", req.getUser());
		write(rsp, Response.TYPE_BROADCAST, Resources.RSP_FORWARD, data);
	}
	
	private List<Operation> makeOperation(String op, Object left, Object right, Boolean vote, String user, Response response) {
		List<Operation> result = new ArrayList<Operation>(2);
		Operation o = new Operation();
		o.setOp(op);
		o.setVote(vote);
		
		try {
		    o.setUserid(new Integer(user));
		} catch (NumberFormatException nfe) {
			Integer userid = dp.getUserIdByName(user);
			if (userid == null) {
				writeError(response, 
						MessageFormat.format(Resources.MSG_ERROR_USER_NOTFOUND, user));
				return null;
			}
			o.setUserid(userid);
		}
		
		Integer featureId1 = getFeatureId(left, response);
		if (featureId1 == null) {
			return null;
		}
		o.setLeft(featureId1);
		
		if (isFeatureAsRightOperand(op)) {
			Integer featureId2 = getFeatureId(right, response);
			if (featureId2 == null) {
				return null;
			}
			o.setRight(featureId2);
			
			if (Resources.OP_ADDCHILD.equals(op) && right instanceof String) {
				// add_name(featureId2, right)
				Operation extra = new Operation();
				extra.setOp(Resources.OP_ADDNAME);
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
	
	private Integer getFeatureId(Object idOrName, Response rsp) {
		if (idOrName instanceof Integer) {
			return (Integer)idOrName;
		} else {
			Integer id = dp.getFeatureIdByName((String)idOrName);
			if (id == null) {
				writeError(rsp, 
						MessageFormat.format(Resources.MSG_ERROR_FEATURE_NOTFOUND,
								idOrName));
				return null;
			}
			return id;
		}
	}
	
	private boolean isFeatureAsRightOperand(String op) {
		for (String name: featureAsRightOperandOp) {
			if (name.equals(op)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
