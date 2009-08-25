package collab.data.bean;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class Votable<T> {
	
	protected T value;
	protected Set<Integer> support = new TreeSet<Integer>(); // List of User ID
	protected Set<Integer> against = new TreeSet<Integer>(); // List of User ID
	
	public Votable() {
		
	}
	
	public Votable(T value) {
		setValue(value);
	}
	
	public synchronized T getValue() {
		return value;
	}

	public synchronized void setValue(T value) { 
		this.value = value;
	}
	
	public synchronized void voteYes(Integer userid) {
		// An user either support or against the value
		support.add(userid);
		against.remove(userid);
	}
	
	public synchronized void voteNo(Integer userid) {
		support.remove(userid);
		against.add(userid);
	}
	
	public void vote(boolean support, Integer userid) {
		if (support) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
	
	public synchronized void setSupport(Set<Integer> support) {
		this.support.clear();
		this.support.addAll(support);
	}

	public synchronized void setAgainst(Set<Integer> against) {
		this.against.clear();
		this.against.addAll(against);
	}

	public synchronized Set<Integer> getSupport() {
		return Collections.unmodifiableSet(support);
	}
	
	public synchronized Set<Integer> getAgainst() {
		return Collections.unmodifiableSet(against);
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			return value.equals(((Votable<?>)obj).getValue());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String toString() {
		//int s1 = support.size(), s2 = against.size();
		return value.toString() + "(" + support.toString() + "/" + against.toString() + ")"; 
	}
}
