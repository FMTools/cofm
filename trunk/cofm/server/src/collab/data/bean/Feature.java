package collab.data.bean;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import collab.data.bean.Operation;
import collab.data.*;

public class Feature {
	
	private Integer id;
	private Votable<Boolean> existence = new Votable<Boolean>(Boolean.valueOf(true));
	private Votable<Boolean> mandatory = new Votable<Boolean>(Boolean.valueOf(true));
	private ConcurrentLinkedQueue<Votable<String>> names = new ConcurrentLinkedQueue<Votable<String>>();
	private ConcurrentLinkedQueue<Votable<String>> descriptions = new ConcurrentLinkedQueue<Votable<String>>();
	private ConcurrentLinkedQueue<Votable<Integer>> require = new ConcurrentLinkedQueue<Votable<Integer>>();
	private ConcurrentLinkedQueue<Votable<Integer>> exclude = new ConcurrentLinkedQueue<Votable<Integer>>();
	private ConcurrentLinkedQueue<Votable<Integer>> children = new ConcurrentLinkedQueue<Votable<Integer>>();
	
	public Feature() {
	
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) { 
		this.id = id;
	}
	
	public void voteFeature(boolean val, int userid) {
		vote(existence, val, userid);
	}
	
	public void voteMandatory(boolean val, int userid) {
		vote(mandatory, val, userid);
	}
	
	public void voteName(String name, boolean support, int userid) {
		vote(true, names, name, support, userid);
	}
	
	public void voteDescription(String des, boolean support, int userid) {
		vote(true, descriptions, des, support, userid);
	}
	
	public void voteRequiring(Integer requireeId, boolean support, int userid) {
		vote(false, require, requireeId, support, userid);
		//if support A requires B, then auto vote against A excludes B
	}
	
	public void voteExcluding(Integer excludeeId, boolean support, int userid) {
		vote(false, exclude, excludeeId, support, userid);
		// if support A excludes B, then auto vote against A requires B
	}
	
	public void voteChild(Integer childId, boolean support, int userid) {
		vote(false, children, childId, support, userid);
	}
	
	private void vote(Votable<Boolean> field, boolean val, int userid) {
		field.vote(field.getValue().equals(val), userid);
	}
	
	private <T> void vote(boolean supportAtMostOne, ConcurrentLinkedQueue<Votable<T>> field, T val, boolean support, int userid) {
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
					otherValSupporters.addAll(Arrays.asList(v.getSupport()));
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
	public String toString() {
		return "Feature: {\n\tId: " + id + 
			   ",\n\tExistence: " + existence.toString() +
			   ",\n\tMandatory: " + mandatory.toString() +
			   ",\n\tName: " + names.toString() +
			   ",\n\tRequiring: " + require.toString() +
			   ",\n\tExcluding: " + exclude.toString() +
			   ",\n\tChildren: " + children.toString() + "\n}";
	}
	
}
