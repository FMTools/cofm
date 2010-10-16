package collab.fm.server.bean.persist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.entity.Attribute;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.Model2;
import collab.fm.server.util.EntityUtil;

public class Model extends DataItem {
	private static Logger logger = Logger.getLogger(Model.class);
	
	private String name;
	private String description;
	
	// Attributes of features in this model
	private Map<String, Attribute> featureAttrs = new HashMap<String, Attribute>();
	
	private Set<Feature> features = new HashSet<Feature>();
	private Set<Relationship> relationships = new HashSet<Relationship>();
	
	// Contributors of this model
	private Set<User> users = new HashSet<User>();
	
	public Model() {
		super();
	}
	
	public Model(Long creator) {
		super(creator);
	}
	
	public String toString() {
		return value().toString();
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof Model)) return false;
		final Model that = (Model) v;
		return that.value().equals(this.value());
	}
	
	public int hashCode() {
		return value().hashCode();
	}
	
	public void addFeature(Feature feature) {
		this.getFeatures().add(feature);
		feature.setModel(this);
	}
	
	public void addRelationship(Relationship r) {
		this.getRelationships().add(r);
		r.setModel(this);
	}
	
	public void addUser(User u) {
		this.getUsers().add(u);
	}
	
	public void addAttributeToFeatures(Attribute a) {
		if (featureAttrs.get(a.getName()) == null) {
			featureAttrs.put(a.getName(), a);
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Attribute> getFeatureAttrs() {
		return featureAttrs;
	}

	public void setFeatureAttrs(Map<String, Attribute> featureAttrs) {
		this.featureAttrs = featureAttrs;
	}

	public Set<Feature> getFeatures() {
		return features;
	}

	private void setFeatures(Set<Feature> features) {
		this.features = features;
	}
	
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	private void setRelationships(Set<Relationship> relationships) {
		this.relationships = relationships;
	}
	
	public Set<User> getUsers() {
		return users;
	}
	
	private void setUsers(Set<User> users) {
		this.users = users;
	}

	public String value() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return String.valueOf(this.getName().hashCode());
	}

	@Override
	public void transfer(Entity2 m) {
		Model2 m2 = (Model2) m;
		super.transfer(m2);
		m2.setName(this.getName());
		m2.setDes(this.getDescription());
		for (User u: this.getUsers()) {
			m2.addUser(u.getId());
		}
	}
}
