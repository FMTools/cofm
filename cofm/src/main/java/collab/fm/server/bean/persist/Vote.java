package collab.fm.server.bean.persist;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


public class Vote {
	
	static Logger logger = Logger.getLogger(Vote.class);
	
	private Long viewCount = 0L;
	private Set<Long> viewers = new HashSet<Long>();
	private Set<Long> supporters = new HashSet<Long>(); 
	private Set<Long> opponents = new HashSet<Long>(); 
	
	public void voteYes(Long userid) {
		// A specific user either support or against the value, but not both
		supporters.add(userid);
		opponents.remove(userid);
	}
	
	public void voteNo(Long userid) {
		// Similar to voteYes().
		supporters.remove(userid);
		opponents.add(userid);
	}
	
	public void vote(boolean yes, Long userid) {
		this.view(userid);
		if (yes) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
	
	public void view(Long userid) {
		viewCount++;
		viewers.add(userid);
	}
	
	public float getSupportRate() {
		if (viewers.isEmpty()) {
			return 0.0f;
		}
		return 1.0f * supporters.size() / viewers.size();
	}
	
	@Override
	public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Vote)) return false;
			final Vote that = (Vote)obj;
			return supporters.equals(that.getSupporters()) &&
				opponents.equals(that.getOpponents()) &&
				viewers.equals(that.getViewers()) &&
				viewCount.equals(that.getViewCount());
	}
	
	@Override
	public int hashCode() { 
		return new Integer(supporters.hashCode() + opponents.hashCode()).hashCode();
	}

	@Override
	public String toString() {
		return "(" + supporters.size() + "/" + opponents.size() + ")"; 
	}
	
	public Set<Long> getSupporters() {
		return supporters;
	}

	public void setSupporters(Set<Long> theSupporters) {
		this.supporters = theSupporters;
	}

	public Set<Long> getOpponents() {
		return opponents;
	}

	public void setOpponents(Set<Long> theOpponents) {
		this.opponents = theOpponents;
	}

	public void setViewers(Set<Long> viewers) {
		this.viewers = viewers;
	}

	public Set<Long> getViewers() {
		return viewers;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public Long getViewCount() {
		return viewCount;
	}
	
}
