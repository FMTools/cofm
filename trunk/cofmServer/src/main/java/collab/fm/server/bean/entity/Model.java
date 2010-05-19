package collab.fm.server.bean.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.transfer.Model2;
import collab.fm.server.bean.transfer.VotableString;
import collab.fm.server.util.LogUtil;

public class Model extends VersionedEntity {
	private static Logger logger = Logger.getLogger(Model.class);
	
	private Long id;
	
	private Set<? extends Votable> names = new HashSet<ModelName>();
	private Set<? extends Votable> descriptions = new HashSet<ModelDescription>();
	
	private Set<Feature> features = new HashSet<Feature>();
	private Set<Relationship> relationships = new HashSet<Relationship>();
	
	private Set<User> users = new HashSet<User>();
	
	public Model() {
		super();
	}
	
	public Model(Long creator) {
		super(creator);
	}
	
	public String toString() {
		return "Feature Model #" + id;
	}
	
	public Model2 transfer() {
		Model2 m = new Model2();
		m.setId(this.getId());
		
		Set<Long> us = new HashSet<Long>();
		for (User u: this.getUsersInternal()) {
			us.add(u.getId());
		}
		m.setUsers(us);
		
		List<VotableString> ns = new ArrayList<VotableString>();
		for (Votable v: this.getNamesInternal()) {
			ModelName mn = (ModelName)v;
			ns.add(mn.transfer());
		}
		m.setNames(ns);
		
		List<VotableString> ds = new ArrayList<VotableString>();
		for (Votable v: this.getDescriptionsInternal()) {
			ModelDescription md = (ModelDescription)v;
			ds.add(md.transfer());
		}
		m.setDscs(ds);
		
		return m;
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof Model)) return false;
		final Model that = (Model) v;
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
	
	public void addFeature(Feature feature) {
		this.getFeaturesInternal().add(feature);
		feature.setModel(this);
	}
	
	public void addRelationship(Relationship r) {
		this.getRelationshipsInternal().add(r);
		r.setModel(this);
	}
	
	public void addUser(User u) {
		this.getUsersInternal().add(u);
	}
	
	public void voteAllName(boolean yes, Long userid) {
		voteAll(this.getNamesInternal(), yes, userid);
	}
	
	public void voteName(String name, boolean yes, Long userid) {
		voteName(name, yes, userid, false);
	} 
	
	public void voteName(String name, boolean yes, Long userid, boolean log) {
		ModelName n = new ModelName(name);
		voteOrAdd(this.getNamesInternal(), n, yes, userid, log);
	}
	
	public void voteAllDescription(boolean yes, Long userid) {
		voteAll(this.getDescriptionsInternal(), yes, userid);
	}
	
	public void voteDescription(String des, boolean yes, Long userid) {
		voteDescription(des, yes, userid, false);
	}
	
	public void voteDescription(String des, boolean yes, Long userid, boolean log) {
		ModelDescription d = new ModelDescription(des);
		voteOrAdd(this.getDescriptionsInternal(), d, yes, userid, log);
	}
	
	@SuppressWarnings("unchecked")
	private void voteOrAdd(Set field, Votable val, boolean yes, Long userid, boolean log) {
		// If existed then vote
		for (Object obj: field) {
			Votable v = (Votable)obj;
			if (v.equals(val)) {
				v.vote(yes, userid);
				if (log) {
					logger.info(LogUtil.logOp(userid, LogUtil.boolToVote(yes),
							LogUtil.modelOrAttrToStr(LogUtil.OBJ_VALUE_OF_MODEL_ATTR,
									id, val)));
				}
				// If no supporters after this vote, remove v from the Set
				if (v.getSupporterNum() <= 0) {
					field.remove(v);
					if (log) {
						logger.info(LogUtil.logOp(userid, LogUtil.OP_REMOVE,
								LogUtil.modelOrAttrToStr(LogUtil.OBJ_VALUE_OF_MODEL_ATTR,
										id, val)));
					}
				}
				return;
			}
		}
		// If not existed then add and vote yes
		if (yes) { // vote 'NO' to a nonexistent value is nonsense.
			val.vote(true, userid);
			val.setCreator(userid);
			field.add(val);
			if (log) {
				logger.info(LogUtil.logOp(userid, LogUtil.OP_CREATE,
						LogUtil.modelOrAttrToStr(LogUtil.OBJ_VALUE_OF_MODEL_ATTR,
								id, val)));
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
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
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
	
	public Set<Feature> getFeatures() {
		return Collections.unmodifiableSet(getFeaturesInternal());
	}

	private void setFeaturesInternal(Set<Feature> features) {
		this.features = features;
	}

	private Set<Feature> getFeaturesInternal() {
		return features;
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
	
	public Set<User> getUsers() {
		return Collections.unmodifiableSet(getUsersInternal());
	}
	
	private void setUsersInternal(Set<User> users) {
		this.users = users;
	}
	
	private Set<User> getUsersInternal() {
		return users;
	}
}
