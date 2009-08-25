package collab.data.bean;

import java.util.*;

public class Feature {
	
	private Integer id;
	private Votable<Boolean> existence = new Votable<Boolean>(Boolean.valueOf(true));
	private Votable<Boolean> mandatory = new Votable<Boolean>(Boolean.valueOf(true));
	private List<Votable<String>> names = new LinkedList<Votable<String>>();
	private List<Votable<String>> descriptions = new LinkedList<Votable<String>>();
	private List<Votable<Integer>> require = new LinkedList<Votable<Integer>>();
	private List<Votable<Integer>> exclude = new LinkedList<Votable<Integer>>();
	private List<Votable<Integer>> children = new LinkedList<Votable<Integer>>();
	
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
	
	public synchronized Integer[] require() {
		return toValue(require).toArray(new Integer[0]);
	}
	
	public synchronized Integer[] exclude() {
		return toValue(exclude).toArray(new Integer[0]);
	}
	
	public synchronized Integer[] children() {
		return toValue(children).toArray(new Integer[0]);
	}
	
	public synchronized void voteFeature(Boolean val, Integer userid) {
		vote(existence, val, userid);
	}
	
	public synchronized void voteMandatory(Boolean val, Integer userid) {
		vote(mandatory, val, userid);
		//voteFeature(true, userid);
	}
	
	public synchronized void voteName(String name, Boolean support, Integer userid) {
		vote(true, names, name, support, userid);
		//voteFeature(true, userid);
	}
	
	public synchronized void voteDescription(String des, Boolean support, Integer userid) {
		vote(true, descriptions, des, support, userid);
		//voteFeature(true, userid);
	}
	
	public synchronized void voteRequiring(Integer requireeId, Boolean support, Integer userid) {
		vote(false, require, requireeId, support, userid);
		if (support) {
			voteExcluding(requireeId, false, userid);
		}
		//voteFeature(true, userid);
		//if support A requires B, then auto vote against A excludes B
	}
	
	public synchronized void voteExcluding(Integer excludeeId, Boolean support, Integer userid) {
		vote(false, exclude, excludeeId, support, userid);
		if (support) {
			voteRequiring(excludeeId, false, userid);
		}
		//voteFeature(true, userid);
		// if support A excludes B, then auto vote against A requires B
	}
	
	public synchronized void voteChild(Integer childId, Boolean support, Integer userid) {
		vote(false, children, childId, support, userid);
		//voteFeature(true, userid);
	}
	
	private void vote(Votable<Boolean> field, boolean val, int userid) {
		field.vote(field.getValue().equals(val), userid);
	}
	
	private <T> void vote(boolean supportAtMostOne, List<Votable<T>> field, T val, boolean support, int userid) {
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
	}

	@Override
	public synchronized String toString() {
		return "Feature: {\n\tId: " + id + 
			   ",\n\tExistence: " + existence.toString() +
			   ",\n\tMandatory: " + mandatory.toString() +
			   ",\n\tName: " + names.toString() +
			   ",\n\tRequiring: " + require.toString() +
			   ",\n\tExcluding: " + exclude.toString() +
			   ",\n\tChildren: " + children.toString() + "\n}";
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

	public synchronized List<Votable<Integer>> getRequire() {
		return Collections.unmodifiableList(require);
	}

	public synchronized void setRequire(List<Votable<Integer>> require) {
		this.require = require;
	}

	public synchronized List<Votable<Integer>> getExclude() {
		return Collections.unmodifiableList(exclude);
	}

	public synchronized void setExclude(List<Votable<Integer>> exclude) {
		this.exclude = exclude;
	}

	public synchronized List<Votable<Integer>> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public synchronized void setChildren(List<Votable<Integer>> children) {
		this.children = children;
	}
	
}
