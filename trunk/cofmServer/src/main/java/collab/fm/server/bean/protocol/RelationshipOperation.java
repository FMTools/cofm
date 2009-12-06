package collab.fm.server.bean.protocol;

import java.util.List;

import collab.fm.server.bean.entity.Feature;

public abstract class RelationshipOperation extends Operation {
	
	/**
	 * A null relationshipId means a new relationship is created.
	 */
	protected Long relationshipId;
	
	/**
	 * Relationship type
	 */
	protected String type; 
	
	public abstract List<Feature> declaredInvolvedFeatures();
	protected abstract boolean typeValid();
	
	public boolean valid() {
		if (super.valid() && userid != null) {
			return typeValid();
		}
		return false;
	}
	
	public Long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
