package collab.fm.server.bean.entity;

public interface Votable {
	public static final Long VOID_CREATOR = -22L;
	
	public Long getId();
	
	public void vote(boolean yes, Long userid);
	public Vote getVote();
	
	public String toValueString();
	
	public void setCreator(Long id);
	public Long getCreator();
	public boolean hasCreator();
	
	public int getSupporterNum();
	public int getOpponentNum();
	
}
