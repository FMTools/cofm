package collab.data.bean;

import java.util.SortedSet;
import java.util.TreeSet;


public class Votable<T> {
	
	protected T value;
	protected SortedSet<Integer> support = new TreeSet<Integer>(); // List of User ID
	protected SortedSet<Integer> against = new TreeSet<Integer>(); // List of User ID
	
	public Votable() {
		
	}
	
	public Votable(T value) {
		setValue(value);
	}
	
	public T getValue() {
		return value;
	}

	protected void setValue(T value) { // for Hibernate
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
	
	public Integer[] getSupport() {
		return support.toArray(new Integer[0]);
	}
	
	public Integer[] getAgainst() {
		return against.toArray(new Integer[0]);
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
