package collab.fm.server.bean.entity;

import java.util.*;


public class Feature implements Votable {
	
	//TODO: further mapping needs to remove the 'transient' keywords.
	private Long id;
	
	private Vote existence = new Vote();
	private Vote optionality = new Vote();
	private Set<? extends Votable> names = new HashSet<FeatureName>();
	private Set<? extends Votable> descriptions = new HashSet<FeatureDescription>();
		
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
	
	public void vote(boolean yes, Long userid) {
		existence.vote(yes, userid);
	}
	
	public void voteOptionality(boolean yes, Long userid) {
		optionality.vote(yes, userid);
	}
	
	public void voteAllName(boolean yes, Long userid) {
		voteAll(names, yes, userid);
	}
	
	public void voteName(String name, boolean yes, Long userid) {
		FeatureName n = new FeatureName(name);
		voteOrAdd(names, n, yes, userid);
	}
	
	public void voteAllDescription(boolean yes, Long userid) {
		voteAll(descriptions, yes, userid);
	}
	
	public void voteDescription(String des, boolean yes, Long userid) {
		FeatureDescription d = new FeatureDescription(des);
		voteOrAdd(descriptions, d, yes, userid);
	}
	
	@SuppressWarnings("unchecked")
	private void voteOrAdd(Set field, Votable val, boolean yes, Long userid) {
		// If existed then vote
		for (Object obj: field) {
			Votable v = (Votable)obj;
			if (v.equals(val)) {
				v.vote(yes, userid);
				return;
			}
		}
		// If not existed then add and vote yes
		if (yes) { // vote 'NO' to a nonexistent value is nonsense.
			val.vote(true, userid);
			field.add(val);
		}
	}
	
	private void voteAll(Set<? extends Votable> field, boolean yes, Long userid) {
		for (Votable v: field) {
			v.vote(yes, userid);
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
		return "{\n\tId: " + id + 
			   ",\n\tExistence: " + existence.toString() +
			   ",\n\tOptionality: " + optionality.toString() +
			   ",\n\tName: " + names.toString() + 
			   ",\n\tDescription:" + descriptions.toString() + "\n}";
	}

	public Vote getExistence() {
		return existence;
	}

	private void setExistence(Vote existence) {
		this.existence = existence;
	}

	public Vote getOptionality() {
		return optionality;
	}

	private void setOptionality(Vote optionality) {
		this.optionality = optionality;
	}

	public Set<? extends Votable> getNames() {
		return Collections.unmodifiableSet(getNamesInternal());
	}
	
	private Set<? extends Votable> getNamesInternal() {
		return names;
	}
	
	private void setNamesInternal(Set<? extends Votable> names) {
		this.names = names;
	}

	public Set<? extends Votable> getDescriptions() {
		return Collections.unmodifiableSet(getDescriptionsInternal());
	}

	private Set<? extends Votable> getDescriptionsInternal() {
		return descriptions;
	}
	
	private void setDescriptionsInternal(Set<? extends Votable> descriptions) {
		this.descriptions = descriptions;
	}

	public boolean equals(Votable v) {
		throw new UnsupportedOperationException();
	}


	
}
