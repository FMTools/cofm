package collab.fm.server.bean.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Relationship implements Votable{
	
	protected Long id;
	protected Vote existence = new Vote();
	protected String type;
	
	protected Set<Feature> features = new HashSet<Feature>();
	
	public Relationship() {
		
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public Vote getExistence() {
		return existence;
	}

	public void setExistence(Vote existence) {
		this.existence = existence;
	}

	public boolean equals(Votable v) {
		return true;
	}

	public void vote(boolean yes, Long userid) {
		existence.vote(yes, userid);		
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

	protected void setFeaturesInternal(Set<Feature> features) {
		this.features = features;
	}

	protected Set<Feature> getFeaturesInternal() {
		return this.features;
	}
	
	protected void addFeature(Feature feature) {
		features.add(feature);
	}
	
	protected void reset() {
		features.clear();
	}
}
