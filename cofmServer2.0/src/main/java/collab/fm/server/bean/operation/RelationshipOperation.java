package collab.fm.server.bean.operation;

import java.util.List;

import collab.fm.server.bean.entity.Feature;

public class RelationshipOperation extends Operation {
	
	/**
	 * A null relationshipId means a new relationship is created.
	 */
	protected Long relationshipId;
	
	/**
	 * Relationship type
	 */
	protected String type; 
	
	public boolean valid() {
		if (super.valid()) {
			return userid != null && modelId != null;
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
