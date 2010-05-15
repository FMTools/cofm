package collab.fm.server.bean.entity;

public interface Votable {
	public void vote(boolean yes, Long userid);
	public Vote getVote();
	public Long getCreator();
	public int getSupporterNum();
	public int getOpponentNum();
}
