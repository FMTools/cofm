package collab.fm.server.bean.persist;

public interface Votable {
	public static final Long VOID_CREATOR = -1L;
	
	public void vote(boolean yes, Long userid);
	
	public Vote getVote();
	public int getSupporterNum();
	public int getOpponentNum();
	
	public String toValueString();
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
}
