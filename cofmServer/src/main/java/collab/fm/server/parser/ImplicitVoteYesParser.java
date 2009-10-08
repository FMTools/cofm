package collab.fm.server.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import collab.fm.server.bean.Feature;
import collab.fm.server.bean.Operation;
import collab.fm.server.storage.DataProvider;
import collab.fm.server.util.Resources;

public class ImplicitVoteYesParser implements Parser {

	private DataProvider dp;
	
	private static final String[] binaryOps = {
		Resources.OP_ADDEXCLUDE,
		Resources.OP_ADDREQUIRE
	};
	
	public ImplicitVoteYesParser(DataProvider dp) {
		this.dp = dp;
	}
	
	public boolean isValid(Operation op) {
		// Accept all ops
		return true;
	}

	public List<Operation> parse(Operation op, boolean needImplicitVotes) {
		// Ignore the 'needImplicitVotes' parameter
		List<Operation> result = voteYesForAncestors(op.getLeft(), op.getUserid());
		if (result != null) {
			if (ArrayUtils.contains(binaryOps, op.getName())) {
				try {
					int rightId = Integer.parseInt((String)op.getRight());
					List<Operation> right = voteYesForAncestors(rightId, op.getUserid());
					if (right != null) {
						result.addAll(right);
					}
				} catch (Exception e) {
					// ignored
				}
			}
			return result;
		}
		return null;
	}
	
	private List<Operation> voteYesForAncestors(Integer id, Integer userid) {
		List<Feature> ancestors = dp.getAncestorsById(id);
		if (ancestors != null) {
			List<Operation> ops = new ArrayList<Operation>();
			for (Feature f: ancestors) {
				Operation op = new Operation();
				op.setName(Resources.OP_SETEXT);
				op.setLeft(f.getId());
				op.setVote(true);
				op.setUserid(userid);
				ops.add(dp.commitOperation(op));
			}
			return ops;
		}
		return null;
	}

}
