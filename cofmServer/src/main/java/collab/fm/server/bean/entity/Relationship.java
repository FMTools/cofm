package collab.fm.server.bean.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Relationship implements Votable{
	
	protected int version;
	protected Long id;
	protected Model model;
	
	protected Vote existence = new Vote();
	protected String type;
	
	protected Set<Feature> features = new HashSet<Feature>();
	
	public Relationship() {
		
	}

	public String toString() {
		return "vote=" + getExistence().toString() + " id=" + id + " type=" + type;
	}
	
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Vote getExistence() {
		return existence;
	}

	public void setExistence(Vote existence) {
		this.existence = existence;
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof Relationship)) return false;
		final Relationship that = (Relationship)v;
		if (getId() != null) {
			return getId().equals(that.getId());
		}
		return getExistence().equals(that.getExistence());
	}

	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		}
		return getExistence().hashCode();
	}
	
	public void vote(boolean yes, Long userid) {
		this.getExistence().vote(yes, userid);		
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Set<Feature> getFeatures() {
		return Collections.unmodifiableSet(getFeaturesInternal());
	}

	public int getOpponentNum() {
		return existence.getOpponents().size();
	}

	public int getSupporterNum() {
		return existence.getSupporters().size();
	}
	
	protected void setFeaturesInternal(Set<Feature> features) {
		this.features = features;
	}

	protected Set<Feature> getFeaturesInternal() {
		return this.features;
	}
	
	protected void addFeature(Feature feature) {
		this.getFeaturesInternal().add(feature);
	}
	
	protected void reset() {
		this.getFeaturesInternal().clear();
	}
}
