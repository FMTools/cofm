package collab.data.bean;

import java.util.concurrent.ConcurrentLinkedQueue;

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

	private void setId(Integer id) { // for Hibernate
		this.id = id;
	}
	
	public void voteFeature(boolean val, int userid) {
		vote(existence, val, userid);
	}
	
	public void voteMandatory(boolean val, int userid) {
		vote(mandatory, val, userid);
	}
	
	public void voteName(String name, boolean support, int userid) {
		vote(names, name, support, userid);
	}
	
	public void voteDescription(String des, boolean support, int userid) {
		vote(descriptions, des, support, userid);
	}
	
	public void voteRequiring(Integer requireeId, boolean support, int userid) {
		vote(require, requireeId, support, userid);
	}
	
	public void voteExcluding(Integer excludeeId, boolean support, int userid) {
		vote(exclude, excludeeId, support, userid);
	}
	
	public void voteChild(Integer childId, boolean support, int userid) {
		vote(children, childId, support, userid);
	}
	
	private void vote(Votable<Boolean> field, boolean val, int userid) {
		field.support(val, userid);
	}
	
	private <T> void vote(ConcurrentLinkedQueue<Votable<T>> field, T val, boolean support, int userid) {
		Votable<T> v = new Votable<T>(val);
		if (field.contains(v)) {
			for (Votable<T> vot: field) {
				if (support) {
					vot.support(val, userid);
				} else {
					vot.against(val, userid);
				}
			}
		} else if (support) {
			v.voteYes(userid);
			for (Votable<T> vot: field) {
				vot.voteNo(userid);
			}
			field.add(v);
		}
		// NOTE: vote against an inexistent value is meaningless, so
		// there's no "else if (not support) {...}"
	}
}
