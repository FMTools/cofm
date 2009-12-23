package collab.fm.server.bean.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


public class Vote {
	
	static Logger logger = Logger.getLogger(Vote.class);
	
	private Set<Long> supporters = new HashSet<Long>(); 
	private Set<Long> opponents = new HashSet<Long>(); 
	
	public Vote() {
	}
	
	private Set<Long> getSupportersInternal() {
		return supporters;
	}

	private void setSupportersInternal(Set<Long> theSupporters) {
		this.supporters = theSupporters;
	}

	private Set<Long> getOpponentsInternal() {
		return opponents;
	}

	private void setOpponentsInternal(Set<Long> theOpponents) {
		this.opponents = theOpponents;
	}
	
	public Set<Long> getSupporters() {
		return Collections.unmodifiableSet(getSupportersInternal());
	}
	
	public Set<Long> getOpponents() {
		return Collections.unmodifiableSet(getOpponentsInternal());
	}
	
	public void voteYes(Long userid) {
		// A specific user either support or against the value
		getSupportersInternal().add(userid);
		getOpponentsInternal().remove(userid);
	}
	
	public void voteNo(Long userid) {
		getSupportersInternal().remove(userid);
		getOpponentsInternal().add(userid);
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
			return getSupportersInternal().equals(that.getSupporters()) &&
				getOpponentsInternal().equals(that.getOpponents());
	}
	
	@Override
	public int hashCode() { 
		return new Integer(getSupportersInternal().hashCode() + getOpponentsInternal().hashCode()).hashCode();
	}

	@Override
	public String toString() {
		return "(" + getSupportersInternal().toString() + "/" + getOpponentsInternal().toString() + ")"; 
	}
}
