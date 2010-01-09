package collab.fm.server.bean.entity;

import collab.fm.server.bean.transfer.BinaryRelation2;
import collab.fm.server.util.BeanUtil;

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
	
	public BinaryRelation2 transfer() {
		BinaryRelation2 br = new BinaryRelation2();
		br.setId(this.getId());
		br.setLeft(this.getLeftFeatureId());
		br.setRight(this.getRightFeatureId());
		br.setType(this.getType());
		br.setV0(BeanUtil.cloneSet(this.getExistence().getOpponents()));
		br.setV1(BeanUtil.cloneSet(this.getExistence().getSupporters()));
		
		return br;
	}
}
