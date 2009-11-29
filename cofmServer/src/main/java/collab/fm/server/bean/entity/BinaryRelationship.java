package collab.fm.server.bean.entity;

import java.util.Arrays;
import java.util.Collection;

public class BinaryRelationship extends Relationship {
	
	private String type;
	private Long leftFeatureId;
	private Long rightFeatureId;
	
	public BinaryRelationship() {
		
	}

	public Long getLeftFeatureId() {
		return leftFeatureId;
	}
	public void setLeftFeatureId(Long leftFeatureId) {
		this.leftFeatureId = leftFeatureId;
	}
	public Long getRightFeatureId() {
		return rightFeatureId;
	}
	public void setRightFeatureId(Long rightFeatureId) {
		this.rightFeatureId = rightFeatureId;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
