package collab.fm.server.bean.entity;

public class Relationship {
	
	protected Long id;
	protected Vote<Boolean> existence = new Vote<Boolean>(Boolean.TRUE);
	
	public Relationship() {
		
	}

	public void voteExistence(boolean support, Long userid) {
		existence.vote(support, userid);
	}
	
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public Vote<Boolean> getExistence() {
		return existence;
	}

	public void setExistence(Vote<Boolean> existence) {
		this.existence = existence;
	}
}
