package collab.fm.server.bean.entity;

public class BinaryRelationship extends Relationship {
	
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
	
	public void setFeatures(Feature left, Feature right) {
		reset();
		setLeftFeatureId(left.getId());
		setRightFeatureId(right.getId());
		addFeature(left);
		addFeature(right);
		
		// Maintain the many-to-many association between Feature and Relationship
		left.addRelationship(this);
		right.addRelationship(this);
	}
}
