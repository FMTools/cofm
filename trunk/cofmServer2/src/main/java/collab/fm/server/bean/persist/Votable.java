package collab.fm.server.bean.persist;

public interface Votable {
	public static final Long VOID_CREATOR = -1L;
	
	/**
	 * 
	 * @param yes
	 * @param userid
	 * @return false if the vote leads to removal of the entity, true if otherwise.
	 */
	public boolean vote(boolean yes, Long userid);
	
	public Vote getVote();
	public int getSupporterNum();
	public int getOpponentNum();
	
	public String value();
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
}
