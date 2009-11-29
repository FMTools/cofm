package collab.fm.server.bean.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Vote<T> {
	
	private T value;
	private Set<Long> supporters = new HashSet<Long>(); // Set of User ID
	private Set<Long> opponents = new HashSet<Long>(); // Set of User ID
	
	public Vote() {
		
	}
	
	public Set<Long> getSupporters() {
		return Collections.unmodifiableSet(supporters);
	}

	public void setSupporters(Set<Long> supporters) {
		this.supporters = supporters;
	}

	public Set<Long> getOpponents() {
		return Collections.unmodifiableSet(opponents);
	}

	public void setOpponents(Set<Long> opponents) {
		this.opponents = opponents;
	}

	public Vote(T value) {
		setValue(value);
	}
	
	private void voteYes(Long userid) {
		// A specific user either support or against the value
		supporters.add(userid);
		opponents.remove(userid);
	}
	
	private void voteNo(Long userid) {
		supporters.remove(userid);
		opponents.add(userid);
	}
	
	public void vote(boolean support, Long userid) {
		if (support) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
	
	public T getValue() {
		return value;
	}

	public void setValue(T value) { 
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			return value.equals(((Vote<?>)obj).getValue());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return value.toString() + "(" + supporters.toString() + "/" + opponents.toString() + ")"; 
	}
}
