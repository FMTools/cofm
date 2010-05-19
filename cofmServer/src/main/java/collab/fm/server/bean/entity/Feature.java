package collab.fm.server.bean.entity;

import java.util.*;

import org.apache.log4j.Logger;

import collab.fm.server.bean.transfer.Feature2;
import collab.fm.server.bean.transfer.VotableString;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.LogUtil;

//NOTE: the vote... methods have two versions:
// - with modelId: call this version for logging modeling progress.
// - without modelId: no logging (suitable for propagated votes).

public class Feature extends VersionedEntity implements Votable {
	
	private static Logger logger = Logger.getLogger(Feature.class);
	
	private Long id;
	private Model model;

	private Vote existence = new Vote();
	private Vote optionality = new Vote();
	private Set<? extends Votable> names = new HashSet<FeatureName>();
	private Set<? extends Votable> descriptions = new HashSet<FeatureDescription>();
	
	private Set<Relationship> relationships = new HashSet<Relationship>();
	
	public Feature() {
		super();
	}
	
	public Feature(Long creator) {
		super(creator);
	}
	
	public Feature2 transfer() {
		Feature2 f = new Feature2();
		f.setId(this.getId());
		f.setV0(BeanUtil.cloneSet(this.getExistence().getOpponents()));
		f.setV1(BeanUtil.cloneSet(this.getExistence().getSupporters()));
		f.setOpt0(BeanUtil.cloneSet(this.getOptionality().getOpponents()));
		f.setOpt1(BeanUtil.cloneSet(this.getOptionality().getSupporters()));
		
		Set<Long> rels = new HashSet<Long>();
		for (Relationship rel: this.getRelationshipsInternal()) {
			rels.add(rel.getId());
		}
		f.setRels(rels);
		
		List<VotableString> ns = new ArrayList<VotableString>();
		for (Votable v: this.getNamesInternal()) {
			FeatureName fn = (FeatureName)v;
			ns.add(fn.transfer());
		}
		f.setNames(ns);
		
		List<VotableString> ds = new ArrayList<VotableString>();
		for (Votable v: this.getDescriptionsInternal()) {
			FeatureDescription fd = (FeatureDescription)v;
			ds.add(fd.transfer());
		}
		f.setDscs(ds);
		
		return f;
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
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * NOTE: never call this method directly, unless it is a subclass of Relationship.
	 * See BinaryRelationship.setFeatures(left, right) for an example. 
	 */
	public void addRelationship(Relationship r) {
		this.getRelationshipsInternal().add(r);
	}
	
	public void vote(boolean yes, Long userid) {
		vote(yes, userid, -1L);
	}
	
	public void vote(boolean yes, Long userid, Long modelId) {
		this.getExistence().vote(yes, userid);
		if (modelId > 0) {
			logger.info(LogUtil.logOp(userid, LogUtil.boolToVote(yes), 
					LogUtil.featureOrAttrToStr(LogUtil.OBJ_FEATURE,
							modelId, id, "")));
		}
	}
	
	public void voteOptionality(boolean yes, Long userid) {
		voteOptionality(yes, userid, -1L);
	}
	
	public void voteOptionality(boolean yes, Long userid, Long modelId) {
		this.getOptionality().vote(yes, userid);
		if (modelId > 0) {
			logger.info(LogUtil.logOp(userid, LogUtil.boolToVote(yes),
					LogUtil.featureOrAttrToStr(LogUtil.OBJ_VALUE,
							modelId, id, "Optionality")));
		}
	}
	
	public void voteAllName(boolean yes, Long userid) {
		voteAll(this.getNamesInternal(), yes, userid);
	}
	
	public void voteName(String name, boolean yes, Long userid) {
		voteName(name, yes, userid, -1L);
	}
	
	public void voteName(String name, boolean yes, Long userid, Long modelId) {
		FeatureName n = new FeatureName(name);
		voteOrAdd(this.getNamesInternal(), n, yes, userid, modelId);
	}
	
	public void voteAllDescription(boolean yes, Long userid) {
		voteAll(this.getDescriptionsInternal(), yes, userid);
	}
	
	public void voteDescription(String des, boolean yes, Long userid) {
		voteDescription(des, yes, userid, -1L);
	}
	
	public void voteDescription(String des, boolean yes, Long userid, Long modelId) {
		FeatureDescription d = new FeatureDescription(des);
		voteOrAdd(this.getDescriptionsInternal(), d, yes, userid, modelId);
	}
	
	@SuppressWarnings("unchecked")
	private void voteOrAdd(Set field, Votable val, boolean yes, Long userid, Long modelId) {
		// Now I use "modelId" for logging purpose only.
		// If existed then vote
		for (Object obj: field) {
			Votable v = (Votable)obj;
			if (v.equals(val)) {
				v.vote(yes, userid);
				if (modelId > 0) {
					logger.info(LogUtil.logOp(userid, LogUtil.boolToVote(yes),
							LogUtil.featureOrAttrToStr(LogUtil.OBJ_VALUE,
									modelId, id, val)));
				}
				// If no supporters after this vote, remove v from the Set.
				if (v.getSupporterNum() <= 0) {
					field.remove(v);
					if (modelId > 0) {
						logger.info(LogUtil.logOp(userid, LogUtil.OP_REMOVE,
								LogUtil.featureOrAttrToStr(LogUtil.OBJ_VALUE,
										modelId, id, val)));
					}
				}
				return;
			}
		}
		// If not existed then add and vote yes
		if (yes) { // vote 'NO' to a nonexistent value is nonsense.
			val.vote(true, userid);
			field.add(val);
			if (modelId > 0) {
				logger.info(LogUtil.logOp(userid, LogUtil.OP_CREATE,
						LogUtil.featureOrAttrToStr(LogUtil.OBJ_VALUE,
								modelId, id, val)));
			}
		}
	}
	
	
	private void voteAll(Set<? extends Votable> field, boolean yes, Long userid) {
		for (Votable v: field) {
			v.vote(yes, userid);
			if (v.getSupporterNum() <= 0) {
				field.remove(v);
			}
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
		return "Feature #" + id;
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
	
	public Set<Relationship> getRelationships() {
		return Collections.unmodifiableSet(getRelationshipsInternal());
	}

	private void setRelationshipsInternal(Set<Relationship> relationships) {
		this.relationships = relationships;
	}

	private Set<Relationship> getRelationshipsInternal() {
		return relationships;
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof Feature)) return false;
		final Feature that = (Feature)v;
		if (getId() != null) {
			return getId().equals(that.getId());
		} 
		return getNames().equals(that.getNames());
	}	
	
	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		}
		return getNames().hashCode();
	}

	public int getOpponentNum() {
		return this.existence.getOpponents().size();
	}

	public int getSupporterNum() {
		return this.existence.getSupporters().size();
	}

	public Vote getVote() {
		return this.existence;
	}

	public boolean hasCreator() {
		return true;
	}
}
