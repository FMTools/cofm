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
	
	// NOTES: For a group of candidates, say {A, B, C}. 
	//  Support A means against B and C; that is, support one means against others.
	//  But against one does NOT mean support others, maybe you want to against them all; that's
	//  why the against() method has no "else" part in it.
	public void support(T val, Integer userid) {
		if (val.equals(value)) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
	
	public void against(T val, Integer userid) {
		if (val.equals(value)) {
			voteNo(userid);
		} 
		// NO else part here. See the "NOTES" above.
	}
}
