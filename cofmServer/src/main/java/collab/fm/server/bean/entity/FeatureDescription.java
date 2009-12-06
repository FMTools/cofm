package collab.fm.server.bean.entity;

public class FeatureDescription implements Votable {

	private Long id;
	private String value;
	private Vote vote = new Vote();
	
	private FeatureDescription() {
		
	}
	
	public FeatureDescription(String des, boolean yes, Long userid) {
		setValue(des);
		vote(yes, userid);
	}
	
	public FeatureDescription(String des) {
		setValue(des);
	}
	
	public String toString() {
		return value + vote.toString();
	}
	
	public void vote(boolean yes, Long userid) {
		vote.vote(yes, userid);
	}
	
	public boolean equals(Votable v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof FeatureDescription)) return false;
		final FeatureDescription that = (FeatureDescription)v;
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
	
	
}
