package collab.fm.server.bean.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.bean.entity.attr.Value;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.Model2;
import collab.fm.server.util.EntityUtil;

public class Model extends Entity implements AttributeSet {
	private static Logger logger = Logger.getLogger(Model.class);
	
	// Attributes: key = Attr_Name
	private Map<String, Attribute> attrs = new HashMap<String, Attribute>();
	
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
	
	public void addAttribute(Attribute a) {
		if (attrs.get(a.getName()) == null) {
			attrs.put(a.getName(), a);
		}
	}
	
	public boolean voteOrAddValue(String attrName, String val, boolean yes, Long userId) {
		Attribute attr = attrs.get(attrName);
		if (attr == null) {
			return false;
		}
		Value v = new Value(userId);
		v.setStrVal(val);
		return attr.voteOrAddValue(v, yes, userId);
	}
	
	public Map<String, Attribute> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, Attribute> attrs) {
		this.attrs = attrs;
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
		return String.valueOf(this.getAttrs().hashCode());
	}

	public Attribute getAttribute(String attrName) {
		return attrs.get(attrName);
	}
	
	@Override
	public void transfer(Entity2 m) {
		Model2 m2 = (Model2) m;
		super.transfer(m2);
		
		for (User u: this.getUsers()) {
			m2.addUser(u.getId());
		}
		
		for (Map.Entry<String, Attribute> e: this.getAttrs().entrySet()) {
			m2.addAttr(EntityUtil.transferFromAttr(e.getValue()));
		}
	}
}
