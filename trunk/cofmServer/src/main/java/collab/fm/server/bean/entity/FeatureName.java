package collab.fm.server.bean.entity;

import collab.fm.server.bean.transfer.VotableString;
import collab.fm.server.util.BeanUtil;

public class FeatureName extends VersionedEntity implements Votable {
	
	
	private Long id;
	private String name;
	private Vote vote = new Vote();
	
	private FeatureName() {
		super();
	}
	
	public VotableString transfer() {
		VotableString vs = new VotableString();
		vs.setVal(this.getName());
		vs.setV0(BeanUtil.cloneSet(this.getVote().getOpponents()));
		vs.setV1(BeanUtil.cloneSet(this.getVote().getSupporters()));
		
		return vs;
	}
	
	public FeatureName(String name) {
		super();
		setName(name);
	}
	
	public FeatureName(String name, boolean yes, Long userid) {
		super();
		setName(name);
		vote(yes, userid);
	}
	
	public String toString() {
		return name + this.getVote().toString();
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof FeatureName)) return false;
		final FeatureName that = (FeatureName) v;
		return getName().equals(that.getName()); 
	}
	
	public int hashCode() {
		return getName().hashCode();
	}
	
	public void vote(boolean yes, Long userid) {
		getVote().vote(yes, userid);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Vote getVote() {
		return vote;
	}
	public void setVote(Vote vote) {
		this.vote = vote;
	}
	public int getOpponentNum() {
		return vote.getOpponents().size();
	}

	public int getSupporterNum() {
		return vote.getSupporters().size();
	}
}
