package collab.fm.server.bean.persist;

public interface Votable {
	public static final Long VOID_CREATOR = -1L;
	
	// Return code for operations performed on the vote-able elements.
	public static final int CREATION_EXECUTED = 0;  // (Valid) creating operation.
	public static final int VOTE_EXECUTED = 1;     // (Valid) voting operation.
	public static final int REMOVAL_EXECUTED = 2;  // Remove via voting.
	public static final int INVALID_OPERATION = -1;
	
	public int vote(boolean yes, Long userid);
	
	public Vote getVote();
	public int getSupporterNum();
	public int getOpponentNum();
	
	public String toValueString();
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
}
