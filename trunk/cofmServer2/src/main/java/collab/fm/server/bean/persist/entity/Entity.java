package collab.fm.server.bean.persist.entity;

import java.util.HashMap;
import java.util.Map;

import collab.fm.server.bean.persist.Element;

public class Entity extends Element {
	
	// Attribute-Value map of this entity. Key = AttrName.
	protected Map<String, ValueList> attrs = new HashMap<String, ValueList>();
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Return Votable.CREATION_EXECUTED (if added new value) or VOTE_EXECUTED (if voted on existing value).
	public int voteOrAddValue(Value value, boolean yes, Long userId) {
		if (!valueIsValid(value)) {
			return Element.INVALID_OPERATION;
		}
		boolean isVoting = false;
		// Check for voting
		for (Iterator<Value> it = values.iterator(); it.hasNext();) {
			Value v = it.next();
			boolean hasVoted = false;
			if (v.equals(value)) {
				isVoting = true;
				hasVoted = true;
				v.vote(yes, userId);
			} else if (!this.multipleSupport && yes) {
				// If multipleSupport is disabled and this vote is YES, then we auto vote NO to other values
				// (NOTE: if this vote is NO, we do nothing.)
				hasVoted = true;
				v.vote(false, userId);
			}
			if (hasVoted && v.getSupporterNum() <= 0) {
				// If there's no supporters after the vote, then remove this value.
				it.remove();
			}
		}
		if (!isVoting) {
			// The value does not exist, we create it here.
			value.vote(true, userId);
			values.add(value);
		}
		return true;
	}
	
	protected boolean valueIsValid(Value v) {
		return true;
	}

}
