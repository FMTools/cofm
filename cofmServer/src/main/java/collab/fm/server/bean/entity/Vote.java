package collab.fm.server.bean.entity;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;


public class Vote<T> {
	
	private T value;
	private Set<Long> support = new TreeSet<Long>(); // Set of User ID
	private Set<Long> against = new TreeSet<Long>(); // Set of User ID
	
	public Vote() {
		
	}
	
	public Vote(T value) {
		setValue(value);
	}
	
	private void voteYes(Long userid) {
		// A specific user either support or against the value
		support.add(userid);
		against.remove(userid);
	}
	
	private void voteNo(Long userid) {
		support.remove(userid);
		against.add(userid);
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
	
	public void setSupport(Set<Long> support) {
		this.support = support;
	}

	public void setAgainst(Set<Long> against) {
		this.against = against;
	}

	public Set<Long> getSupport() {
		return Collections.unmodifiableSet(support);
	}
	
	public Set<Long> getAgainst() {
		return Collections.unmodifiableSet(against);
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
		return value.toString() + "(" + support.toString() + "/" + against.toString() + ")"; 
	}
}
