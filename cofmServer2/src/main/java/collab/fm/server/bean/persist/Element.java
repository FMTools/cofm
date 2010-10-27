package collab.fm.server.bean.persist;

import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.VotableEntity2;

/**
 * The root class for all elements in the model. Every element is vote-able in CoFM.
 * Sub-classes of Element include Entity, Relation, and Value.
 * @author mark
 *
 */
public abstract class Element extends DataItem implements Votable {

	protected Vote vote = new Vote();
	protected ElementType type;
	
	public Element() {
		super();
	}

	public int getOpponentNum() {
		return vote.getOpponents().size();
	}

	public int getSupporterNum() {
		return vote.getSupporters().size();
	}

	public int vote(boolean yes, Long userid) {
		this.vote.vote(yes, userid);
		if (this.getSupporterNum() <= 0) {
			return DataItem.REMOVAL_EXECUTED;
		} 
		return DataItem.VOTE_EXECUTED;
	}
	
	@Override
	public void transfer(DataItem2 ve) {
		VotableEntity2 ve2 = (VotableEntity2) ve;
		super.transfer(ve2);
		for (Long n: vote.getSupporters()) {
			ve2.addV1(n);
		}
		for (Long n: vote.getOpponents()) {
			ve2.addV0(n);
		}
	}
	
	public Vote getVote() {
		return vote;
	}
	
	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}
	
}
