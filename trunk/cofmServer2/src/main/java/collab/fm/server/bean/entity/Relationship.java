package collab.fm.server.bean.entity;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public abstract class Relationship extends VotableEntity {
	
	private static Logger logger = Logger.getLogger(Relationship.class);
	
	protected Model model;
	
	// The type of relationship
	protected String type;
	
	// Involved features
	protected Set<Feature> features = new HashSet<Feature>();
	
	public Relationship() {
		super();
	}
	
	public Relationship(Long creator) {
		super(creator);
	}

	public String toString() {
		return valueOfRelationship();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (this == null || o == null) return false;
		if (!(o instanceof Relationship)) return false;
		Relationship that = (Relationship) o;
		return this.value().equals(that.value());
	}

	@Override
	public int hashCode() {
		return this.value().hashCode();
	}

	public String value() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return valueOfRelationship();
	}
	
	abstract protected String valueOfRelationship();
	
	@Override
	protected void removeThis() {
		try {
			DaoUtil.getRelationshipDao().delete(this);
		} catch (EntityPersistenceException e) {
			logger.warn("Delete relation failed.", e);
		} catch (StaleDataException e) {
			logger.warn("Delete relation failed.", e);
		}
	}
	
	// Voting YES to relationship needs vote inference, i.e. voting YES to all involved features.
	@Override
	public boolean vote(boolean yes, Long userId) {
		if (yes) {
			// vote inference
			for (Feature f: features) {
				f.vote(true, userId);
				try {
					DaoUtil.getFeatureDao().save(f);
				} catch (EntityPersistenceException e) {
					logger.warn("Vote on feature failed.", e);
				} catch (StaleDataException e) {
					logger.warn("Vote on feature failed.", e);
				}
			}
		}
		return super.vote(yes, userId);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Set<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	protected void addFeature(Feature feature) {
		this.getFeatures().add(feature);
	}
	
	protected void reset() {
		this.getFeatures().clear();
	}
}
