package collab.fm.server.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import collab.fm.server.bean.Feature;
import collab.fm.server.bean.Operation;
import collab.fm.server.storage.DataProvider;
import collab.fm.server.util.Resources;

/**
 * Parser for operations which set or vote on the attributes of features.
 * @author Yi Li
 *
 */
public class FeatureAttrParser implements Parser {
	
	public static final String[] acceptOps = {
		Resources.OP_ADDNAME,
		Resources.OP_ADDDES,
		Resources.OP_SETEXT,
		Resources.OP_SETOPT
	};
	
	
	private ParserManager manager;
	
	public FeatureAttrParser(ParserManager manager) {
		this.manager = manager;
	}
	
	public List<Operation> parse(Operation op, boolean needImplicitVotes) {
		if (!isValid(op)) {
			return null;
		}
		Feature feature = manager.dp().getFeatureById(op.getId());
		if (feature == null) {
			return null;
		}
		boolean implicitYes = true;
		if (Resources.OP_ADDNAME.equals(op.getName())) {
			feature.voteName((String)op.getRight(), op.getVote(), op.getUserid());
		} else if (Resources.OP_ADDDES.equals(op.getName())) {
			feature.voteDescription((String)op.getRight(), op.getVote(), op.getUserid());
		} else if (Resources.OP_SETEXT.equals(op.getName())) {
			feature.voteExistence(op.getVote(), op.getUserid());
			implicitYes = op.getVote();
		} else {
			feature.voteMandatory(op.getVote(), op.getUserid());
		}
		
		List<Operation> result = new ArrayList<Operation>();
		result.add(manager.dp().commitOperation(op));
		
		if (needImplicitVotes && implicitYes) {
			if (!Resources.OP_SETEXT.equals(op.getName())) {
				feature.voteExistence(true, op.getUserid());
			}
			List<Operation> implicits = manager.implicitVoteYes(op);
			if (implicits != null) {
				result.addAll(implicits);
			}
		}
		return result;
	}
	

	public boolean isValid(Operation op) {
		if (!ArrayUtils.contains(acceptOps, op.getName())) {
			return false;
		}
		
		if (Resources.OP_ADDNAME.equals(op.getName()) || Resources.OP_ADDDES.equals(op.getName())) {
			if (!(op.getRight() instanceof String)) {
				return false;
			}
		}
		// NOTE: The right operand will be ignored if op.name == "OP_SETEXT" or "OP_SETOPT"
		return true;
	}

}
