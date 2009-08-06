package collab.data.bean;

import java.util.SortedSet;
import java.util.TreeSet;


public class Votable<T> {
	
	protected T value;
	protected SortedSet<Long> support = new TreeSet<Long>(); // List of User ID
	protected SortedSet<Long> against = new TreeSet<Long>(); // List of User ID
	
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
	
	public synchronized void voteYes(Long userid) {
		// An user either support or against the value
		support.add(userid);
		against.remove(userid);
	}
	
	public synchronized void voteNo(Long userid) {
		support.remove(userid);
		against.add(userid);
	}
	
	public void support(T val, Long userid) {
		if (val.equals(value)) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
	
	public void against(T val, Long userid) {
		if (val.equals(value)) {
			voteNo(userid);
		} 
	}
}
