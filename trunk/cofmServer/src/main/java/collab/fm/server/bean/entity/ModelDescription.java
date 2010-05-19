package collab.fm.server.bean.entity;

import collab.fm.server.bean.transfer.VotableString;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.LogUtil;

public class ModelDescription extends VersionedEntity implements Votable {

	
	private Long id;
	private String value;
	private Vote vote = new Vote();
	
	private ModelDescription() {
		super();
	}
	
	public VotableString transfer() {
		VotableString vs = new VotableString();
		vs.setVal(this.getValue());
		vs.setV0(BeanUtil.cloneSet(this.getVote().getOpponents()));
		vs.setV1(BeanUtil.cloneSet(this.getVote().getSupporters()));
		
		return vs;
	}
	
	public ModelDescription(String des, boolean yes, Long userid) {
		super(userid);
		setValue(des);
		vote(yes, userid);
	}
	
	public ModelDescription(String des) {
		super();
		setValue(des);
	}
	
	public String toString() {
		return "Model Description '" + LogUtil.truncText(value) + "'";
	}
	
	public void vote(boolean yes, Long userid) {
		getVote().vote(yes, userid);
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof ModelDescription)) return false;
		final ModelDescription that = (ModelDescription)v;
		return getValue().equals(that.getValue());
	}
	
	public int hashCode() {
		return getValue().hashCode();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	
	public boolean hasCreator() {
		return true;
	}

}
