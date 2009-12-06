package collab.fm.server.bean.entity;

public class FeatureName implements Votable {
	
	private Long id;
	private String name;
	private Vote vote = new Vote();
	
	private FeatureName() {
		
	}

	public FeatureName(String name) {
		setName(name);
	}
	
	public FeatureName(String name, boolean yes, Long userid) {
		setName(name);
		vote(yes, userid);
	}
	
	public String toString() {
		return name + vote.toString();
	}
	
	public boolean equals(Votable v) {
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
		vote.vote(yes, userid);
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
	
		
}
