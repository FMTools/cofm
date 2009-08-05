package collab.data.bean;

import java.util.SortedSet;
import java.util.TreeSet;


public abstract class Votable {
	
	protected Object value;
	protected SortedSet<Long> support = new TreeSet<Long>(); // List of User ID
	protected SortedSet<Long> against = new TreeSet<Long>(); // List of User ID
	
	public Votable() {
		
	}
	
	public Votable(Object value) {
		setValue(value);
	}
	
	public Object getValue() {
		return value;
	}

	protected void setValue(Object value) { // for Hibernate
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
	
	public void vote(Object val, Long userid) {
		if (val.equals(value)) {
			voteYes(userid);
		} else {
			voteNo(userid);
		}
	}
}
