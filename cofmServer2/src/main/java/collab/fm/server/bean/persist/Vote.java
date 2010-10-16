package collab.fm.server.bean.persist;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


public class Vote {
	
	static Logger logger = Logger.getLogger(Vote.class);
	
	private Set<Long> supporters = new HashSet<Long>(); 
	private Set<Long> opponents = new HashSet<Long>(); 
	
	public Vote() {
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
	
	public void voteYes(Long userid) {
		// A specific user either support or against the value
		supporters.add(userid);
		opponents.remove(userid);
	}
	
	public void voteNo(Long userid) {
		supporters.remove(userid);
		opponents.add(userid);
	}
	
	public void vote(boolean yes, Long userid) {
		if (yes) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Vote)) return false;
			final Vote that = (Vote)obj;
			return supporters.equals(that.getSupporters()) &&
				opponents.equals(that.getOpponents());
	}
	
	@Override
	public int hashCode() { 
		return new Integer(supporters.hashCode() + opponents.hashCode()).hashCode();
	}

	@Override
	public String toString() {
		return "(" + supporters.size() + "/" + opponents.size() + ")"; 
	}
}
