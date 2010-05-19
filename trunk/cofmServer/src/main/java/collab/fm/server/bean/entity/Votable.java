package collab.fm.server.bean.entity;

public interface Votable {
	public static final Long VOID_CREATOR = -1L;
	
	public void vote(boolean yes, Long userid);
	public Vote getVote();
	public Long getCreator();
	public int getSupporterNum();
	public int getOpponentNum();
	public boolean hasCreator();
}
