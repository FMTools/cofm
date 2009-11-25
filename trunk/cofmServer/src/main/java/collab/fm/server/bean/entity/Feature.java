package collab.fm.server.bean.entity;

import java.util.*;


public class Feature {
	
	private Long id;
	
	private Vote<Boolean> existence = new Vote<Boolean>(Boolean.TRUE);
	private Vote<Boolean> optionality = new Vote<Boolean>(Boolean.TRUE);
	private List<Vote<String>> names = new LinkedList<Vote<String>>();
	private List<Vote<String>> descriptions = new LinkedList<Vote<String>>();
	
	public Feature() {
	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) { 
		this.id = id;
	}
	
/*	private <T> List<T> toValue(List<Vote<T>> container) {
		List<T> t = new ArrayList<T>();
		for (Vote<T> v: container) {
			t.add(v.getValue());
		}
		return t;
	}
	
	public String[] names() {
		return toValue(names).toArray(new String[0]);
	}
	
	public String[] descriptions() {
		return toValue(descriptions).toArray(new String[0]);
	}*/
	
	public void voteExistence(Boolean val, Long userid) {
		voteBool(existence, val, userid);
	}
	
	public void voteOptionality(Boolean val, Long userid) {
		voteBool(optionality, val, userid);
	}
	
	public void voteName(String name, Boolean support, Long userid) {
		voteList(names, name, support, userid);
	}
	
	public void voteDescription(String des, Boolean support, Long userid) {
		voteList(descriptions, des, support, userid);
	}
	
	private void voteBool(Vote<Boolean> field, Boolean val, Long userid) {
		field.vote(val, userid);
	}
	
	private <T> void voteList(List<Vote<T>> field, T val, boolean support, Long userid) {
		Vote<T> theVote = new Vote<T>(val);
		for (Vote<T> v: field) {
			if (theVote.equals(v)) {
				v.vote(support, userid);
				return;
			}
		}
		// If we reach here, that means "val" is not existed yet.
		if (support) { // vote 'NO' to a nonexistent value is nonsense.
			theVote.vote(true, userid);
			field.add(theVote);
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
	public String toString() {
		return "Feature: {\n\tId: " + id + 
			   ",\n\tExistence: " + existence.toString() +
			   ",\n\tOptionality: " + optionality.toString() +
			   ",\n\tName: " + names.toString() + "\n}";
	}

	public Vote<Boolean> getExistence() {
		return existence;
	}

	public void setExistence(Vote<Boolean> existence) {
		this.existence = existence;
	}

	public Vote<Boolean> getOptionality() {
		return optionality;
	}

	public void setOptionality(Vote<Boolean> optionality) {
		this.optionality = optionality;
	}

	public List<Vote<String>> getNames() {
		return Collections.unmodifiableList(names);
	}

	public void setNames(List<Vote<String>> names) {
		this.names = names;
	}

	public List<Vote<String>> getDescriptions() {
		return Collections.unmodifiableList(descriptions);
	}

	public void setDescriptions(List<Vote<String>> descriptions) {
		this.descriptions = descriptions;
	}
	
}
