package collab.fm.server.bean.persist;

import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.VotableEntity2;

public abstract class VotableEntity extends DataItem implements Votable {

	protected Vote vote = new Vote();
	
	public VotableEntity() {
		super();
	}
	
	public VotableEntity(Long creator) {
		super(creator);
	}
	
	public int getOpponentNum() {
		return vote.getOpponents().size();
	}

	public int getSupporterNum() {
		return vote.getSupporters().size();
	}

	public Vote getVote() {
		return vote;
	}
	
	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public boolean vote(boolean yes, Long userid) {
		this.vote.vote(yes, userid);
		// If there's no supporters for this entity, delete it now.
		if (this.getSupporterNum() <= 0) {
			removeThis();
			return false;
		}
		return true;
	}
	
	abstract public String value();
	abstract public boolean equals(Object o);
	abstract public int hashCode();
	abstract protected void removeThis();
	
	@Override
	public void transfer(Entity2 ve) {
		VotableEntity2 ve2 = (VotableEntity2) ve;
		super.transfer(ve2);
		for (Long n: vote.getSupporters()) {
			ve2.addV1(n);
		}
		for (Long n: vote.getOpponents()) {
			ve2.addV0(n);
		}
	}
}
