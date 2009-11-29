package collab.fm.server.bean.entity;

import java.util.*;


public class Feature {
	
	//TODO: further mapping needs to remove the 'transient' keywords.
	private Long id;
	
	private Vote<Boolean> existence = new Vote<Boolean>(Boolean.TRUE);
	transient private Vote<Boolean> optionality = new Vote<Boolean>(Boolean.TRUE);
	transient private Collection<Vote<String>> names = new ArrayList<Vote<String>>();
	transient private Collection<Vote<String>> descriptions = new ArrayList<Vote<String>>();
	
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
	
	public void voteAllName(boolean support, Long userid) {
		voteAll(names, support, userid);
	}
	
	public void voteName(String name, Boolean support, Long userid) {
		voteOneInCollection(names, name, support, userid);
	}
	
	public void voteAllDescription(boolean support, Long userid) {
		voteAll(descriptions, support, userid);
	}
	
	public void voteDescription(String des, Boolean support, Long userid) {
		voteOneInCollection(descriptions, des, support, userid);
	}
	
	private void voteBool(Vote<Boolean> field, Boolean val, Long userid) {
		field.vote(val, userid);
	}
	
	private <T> void voteOneInCollection(Collection<Vote<T>> field, T val, boolean support, Long userid) {
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
	
	private <T> void voteAll(Collection<Vote<T>> field, boolean support, Long userid) {
		for (Vote<T> v: field) {
			v.vote(support, userid);
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
	
	public Vote<Boolean> getOptionality() {
		return optionality;
	}

	public Collection<Vote<String>> getNames() {
		return Collections.unmodifiableCollection(getNamesInternal());
	}
	
	public Collection<Vote<String>> getDescriptions() {
		return Collections.unmodifiableCollection(getDescriptionsInternal());
	}
	
	private void setExistence(Vote<Boolean> existence) {
		this.existence = existence;
	}

	private void setOptionality(Vote<Boolean> optionality) {
		this.optionality = optionality;
	}

	private Collection<Vote<String>> getNamesInternal() {
		return names;
	}

	private void setNamesInternal(Collection<Vote<String>> names) {
		this.names = names;
	}

	private Collection<Vote<String>> getDescriptionsInternal() {
		return descriptions;
	}

	private void setDescriptionsInternal(Collection<Vote<String>> descriptions) {
		this.descriptions = descriptions;
	}
	
}
