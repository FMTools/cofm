package collab.data.bean;

import java.util.SortedSet;
import java.util.TreeSet;


public abstract class Votable {
	
	
	protected final Object value;
	protected SortedSet<Long> support = new TreeSet<Long>(); // List of User ID
	protected SortedSet<Long> against = new TreeSet<Long>(); // List of User ID
	
	public Votable() {
		this.value = new Boolean(true);
	}
	
	public Votable(Object value) {
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
	
	public Object value() {
		return value;
	}
	
	public void vote(Object val, Long userid) {
		if (val.equals(value)) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
}
