package collab.util.mock.client;

import org.apache.commons.beanutils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.*;
import collab.util.Utils;
import collab.util.mock.OpBean;

public class Commit implements Handler {
	
	static Logger logger = Logger.getLogger(Commit.class);
	
	public static class Options implements HandlerOptions {
		public static volatile int ratioOfVote = 60;
		
		public static final int NEW = 0;
		public static final int VOTE = 1;
		public static final int RANDOM = 2;
		
		public int type;
		public String opName;
		
		public Options(int type, String opName) {
			this.type = type;
			this.opName = opName;
		}
		
		public boolean voteOnly() {
			if (type == NEW) {
				return false;
			} 
			if (type == VOTE) {
				return true;
			}
			return Utils.randomBool(ratioOfVote);
		}
	}
	
	public volatile int ratioOfVoteYes = 70;
	public volatile int ratioOfAddNewFeature = 40;
	
	private MockClient client;
	
	public Commit() {
		
	}
	public Commit(MockClient client) {
		this.client = client;
		client.addHandler(Resources.REQ_COMMIT, this);
	}
	
	@Override
	public Request send(HandlerOptions options) {
		Options opt = (Options)options;
		Request req = new Request();
		
		OpBean op = new OpBean();
		op.setOp(opt.opName == null ? 
				Operation.NAMES[RandomUtils.nextInt(Operation.NAMES.length)] : opt.opName);
		Feature left = client.randomFeature();
		op.setLeft(selectIdOrName(left.getId(), left.names()));
		op.setRight(getRightOperand(op.getOp(), left, opt.voteOnly()));
		op.setVote(opt.voteOnly() ? Utils.randomBool(ratioOfVoteYes) : true);
		req.setData(op);
		
		req.setName(Resources.REQ_COMMIT);
		User u = client.randomUser();
		req.setUser(selectIdOrName(u.getId(), u.getName()).toString());
		
		return req;
	}
	
	private Object selectIdOrName(Integer id, String name) {
		return Utils.randomSelect(new Object[]{id, name});
	}
	
	private Object selectIdOrName(Integer id, String[] names) {
		String name = (String)Utils.randomSelect(names);
		return selectIdOrName(id, name);
	}
	
	private Object getRightOperand(String op, Feature left, boolean vote) {
		// vote = true: select one attribute from left
		// vote = false: means add new attribute for left, or create new feature
		if (Resources.OP_ADDDES.equals(op)) {
			return selectValue(
					vote,
					left.descriptions(),
					RandomStringUtils.randomAlphabetic(30));
		}
		if (Resources.OP_ADDNAME.equals(op)) {
			return selectValue(
					vote,
					left.names(),
					client.randomFeatureName());
		}
		if (Resources.OP_ADDCHILD.equals(op)) {
			String child;
			if (Utils.randomBool(ratioOfAddNewFeature)) {
				child = client.randomFeatureName();
			} else {
				child = Utils.randomSelect(client.randomFeature().names());
			}
			return selectValue(
					vote,
					left.children(),
					child);
		}
		if (Resources.OP_ADDREQUIRE.equals(op)) {
			return selectValue(
					vote,
					left.require(),
					Utils.randomSelect(client.randomFeature().names()));
		}
		if (Resources.OP_ADDEXCLUDE.equals(op)) {
			return selectValue(
					vote,
					left.exclude(),
					Utils.randomSelect(client.randomFeature().names()));
		}
		return "ignored"; // set_ext and set_opt has no right operand
	}
	
	private <T, U> Object selectValue(boolean vote, T[] oldVals, U newVal) {
		if (vote) {
			return Utils.randomSelect(oldVals);
		}
		return newVal;
	}

	@Override
	public void recv(Response.Body body) {
		Response.Body.Source src = body.getSource();
		if (Resources.RSP_SUCCESS.equals(body.getStatus())) {
			client.onSuccess(body);
		} else if (Resources.RSP_FORWARD.equals(body.getStatus())) {
			client.onForward(body);
			try {
				DynaBean bean = (DynaBean) body.getData();
				String op = (String) bean.get("op");
				Object right = bean.get("right");
				Boolean support = (Boolean) bean.get("vote");
				Integer userid = (Integer) bean.get("user");
				Integer left = (Integer) bean.get("left");
				Feature feat = client.getFeature(left);
				applyOp(feat, op, right, support, userid);
				client.setFeature(feat);
			} catch (Exception e) {
				logger.warn("Can not parse forwarded commit.", e);
			}
		} else if (Resources.RSP_DENIED.equals(body.getStatus())) {
			client.onDenied(body);
		} else {
			client.onError(body);
		}
	}
	
	private void applyOp(Feature f, String op, Object right, Boolean support, Integer userid) {
		if (Resources.OP_ADDCHILD.equals(op)) {
			f.voteChild((Integer)right, support, userid);
		} else if (Resources.OP_ADDDES.equals(op)) {
			f.voteDescription((String)right, support, userid);
		} else if (Resources.OP_ADDEXCLUDE.equals(op)) {
			f.voteExcluding((Integer)right, support, userid);
		} else if (Resources.OP_ADDNAME.equals(op)) {
			f.voteName((String)right, support, userid);
		} else if (Resources.OP_ADDREQUIRE.equals(op)) {
			f.voteRequiring((Integer)right, support, userid);
		} else if (Resources.OP_SETEXT.equals(op)) {
			f.voteFeature(support, userid);
		} else if (Resources.OP_SETOPT.equals(op)) {
			f.voteMandatory(support, userid);
		}
	}
}
