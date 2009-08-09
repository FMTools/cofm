package collab.action;

import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.*;
import collab.server.Controller;
import collab.storage.DataProvider;

public class CommitAction extends Action {
	
	static Logger logger = Logger.getLogger(CommitAction.class);

	public CommitAction(Controller controller,
			DataProvider dp) {
		super(new String[]{Resources.get(Resources.REQ_COMMIT)}, controller, dp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Response> process(Object input) {
		try {
			DynaBean data = (DynaBean)((Request)input).getData();
			String op = (String)data.get(Resources.get(Resources.OP_FIELD_OP));
			Object left = data.get(Resources.get(Resources.OP_FIELD_LEFT));
			Object right = data.get(Resources.get(Resources.OP_FIELD_RIGHT));
			Boolean vote = (Boolean)data.get(Resources.get(Resources.OP_FIELD_VOTE));
			String user = ((Request)input).getUser();
			
			Operation operation = makeOperation(op, left, right, vote, user);
		} catch (Exception e) {
			logger.warn("Invalid request. You may need to check whether RequestValidator has been set and worked as desired.", e);
			return null;
		}
	}
	
	private Operation makeOperation(String op, Object left, Object right, Boolean vote, String user) {
		Operation o = new Operation();
		o.setOp(op);
		o.setVote(vote);
		try {
		    o.setUserid(new Integer(user));
		} catch (NumberFormatException nfe) {
			o.setUserid(dp.getUserIdByName(user));
		}
		if (left instanceof Integer) {
			o.setLeft(left);
		} else {
			o.setLeft(dp.getFea)
		}
	}

}
