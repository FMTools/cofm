package collab.fm.server.bean.entity;

public class BinaryRelationship extends Relationship {
	
	private Long leftFeatureId;
	private Long rightFeatureId;
	
	public BinaryRelationship() {
		
	}
	
	public String toString() {
		return super.toString() + " left=" + leftFeatureId + " right=" + rightFeatureId;
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof BinaryRelationship)) return false;
		final BinaryRelationship that = (BinaryRelationship)v;
		if (getId() != null) {
			return getId().equals(that.getId());
		}
		return getType().equals(that.getType()) 
			&& getLeftFeatureId().equals(that.getLeftFeatureId())
			&& getRightFeatureId().equals(that.getRightFeatureId());
	}
	
	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		}
		return new Integer(getType().hashCode() + getLeftFeatureId().hashCode() + getRightFeatureId().hashCode()).hashCode();
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
