package collab.fm.server.bean.entity;

public class Relationship implements Votable{
	
	protected Long id;
	protected Vote existence = new Vote();
	
	public Relationship() {
		
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public Vote getExistence() {
		return existence;
	}

	public void setExistence(Vote existence) {
		this.existence = existence;
	}

	public boolean valueEquals(Votable v) {
		return true;
	}

	public void vote(boolean yes, Long userid) {
		existence.vote(yes, userid);		
	}
}
