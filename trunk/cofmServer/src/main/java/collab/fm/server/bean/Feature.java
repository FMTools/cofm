package collab.fm.server.bean;

import java.util.*;

public class Feature {
	
	private Integer id;
	private Votable<Boolean> existence = new Votable<Boolean>(Boolean.TRUE);
	private Votable<Boolean> mandatory = new Votable<Boolean>(Boolean.TRUE);
	private List<Votable<String>> names = new LinkedList<Votable<String>>();
	private List<Votable<String>> descriptions = new LinkedList<Votable<String>>();
	
	public Feature() {
	
	}

	public synchronized Integer getId() {
		return id;
	}

	public synchronized void setId(Integer id) { 
		this.id = id;
	}
	
	private <T> List<T> toValue(List<Votable<T>> container) {
		List<T> t = new ArrayList<T>();
		for (Votable<T> v: container) {
			t.add(v.getValue());
		}
		return t;
	}
	
	public synchronized String[] names() {
		return toValue(names).toArray(new String[0]);
	}
	
	public synchronized String[] descriptions() {
		return toValue(descriptions).toArray(new String[0]);
	}
	
	public synchronized void voteExistence(Boolean val, Integer userid) {
		vote(existence, val, userid);
	}
	
	public synchronized void voteMandatory(Boolean val, Integer userid) {
		vote(mandatory, val, userid);
	}
	
	public synchronized void voteName(String name, Boolean support, Integer userid) {
		vote(names, name, support, userid);
	}
	
	public synchronized void voteDescription(String des, Boolean support, Integer userid) {
		vote(descriptions, des, support, userid);
	}
	
	private void vote(Votable<Boolean> field, boolean val, int userid) {
		field.vote(field.getValue().equals(val), userid);
	}
	
	private <T> void vote(List<Votable<T>> field, T val, boolean support, int userid) {
		Votable<T> voting = new Votable<T>(val);
		for (Votable<T> v: field) {
			if (voting.equals(v)) {
				v.vote(support, userid);
				return;
			}
		}
		// If we reach here, that means "val" is not existed yet.
		if (support) { // vote 'NO' to a nonexistent value is nonsense.
			voting.vote(true, userid);
			field.add(voting);
		}
	}
	
	/*private <T> void vote(boolean supportAtMostOne, List<Votable<T>> field, T val, boolean support, int userid) {
		TreeSet<Integer> otherValSupporters = null;
		Votable<T> theVal = new Votable<T>(val);
		boolean isValExisted = field.contains(theVal);
		if (supportAtMostOne) {
			otherValSupporters = new TreeSet<Integer>();
		}
	
		for (Votable<T> v : field) {
			if (v.getValue().equals(val)) {
				v.vote(support, userid);
			} else if (supportAtMostOne) {
				if (support) {
					v.voteNo(userid);
				}
				if (!isValExisted) {
					otherValSupporters.addAll(v.getSupport());
				}
			}
		}
	
		if (!isValExisted && support) {
			theVal.voteYes(userid);
			if (supportAtMostOne) {
				for (Integer user: otherValSupporters) {
					theVal.voteNo(user);
				}
			}
			field.add(theVal);
		}
		//NOTE: if (!isValExisted && !support), that means vote against an inexistent value, which makes no sense.
		//so we ignore those vote
	}*/

	@Override
	public synchronized String toString() {
		return "Feature: {\n\tId: " + id + 
			   ",\n\tExistence: " + existence.toString() +
			   ",\n\tMandatory: " + mandatory.toString() +
			   ",\n\tName: " + names.toString() + "\n}";
	}

	public synchronized Votable<Boolean> getExistence() {
		return existence;
	}

	public synchronized void setExistence(Votable<Boolean> existence) {
		this.existence = existence;
	}

	public synchronized Votable<Boolean> getMandatory() {
		return mandatory;
	}

	public synchronized void setMandatory(Votable<Boolean> mandatory) {
		this.mandatory = mandatory;
	}

	public synchronized List<Votable<String>> getNames() {
		return Collections.unmodifiableList(names);
	}

	public synchronized void setNames(List<Votable<String>> names) {
		this.names = names;
	}

	public synchronized List<Votable<String>> getDescriptions() {
		return Collections.unmodifiableList(descriptions);
	}

	public synchronized void setDescriptions(List<Votable<String>> descriptions) {
		this.descriptions = descriptions;
	}
	
}
