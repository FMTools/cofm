package collab.fm.server.bean.entity;

public class ModelName implements Votable {

    private int version;
	
	private Long id;
	private String name;
	private Vote vote = new Vote();
	
	private ModelName() {
		
	}

	public ModelName(String name) {
		setName(name);
	}
	
	public ModelName(String name, boolean yes, Long userid) {
		setName(name);
		vote(yes, userid);
	}
	
	public String toString() {
		return name + vote.toString();
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof ModelName)) return false;
		final ModelName that = (ModelName) v;
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
