package collab.fm.server.bean.persist;

import collab.fm.server.bean.transfer.BinaryRelation2;
import collab.fm.server.bean.transfer.Entity2;

public class BinaryRelationship extends Relationship {
	
	private Long leftFeatureId;
	private Long rightFeatureId;
	
	public BinaryRelationship() {
		super();
	}
	
	public BinaryRelationship(Long creator) {
		super(creator);
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
	
	@Override
	public void transfer(Entity2 r) {
		BinaryRelation2 r2 = (BinaryRelation2) r;
		super.transfer(r2);
		r2.setLeft(this.getLeftFeatureId());
		r2.setRight(this.getRightFeatureId());
		r2.setType(this.getType());
	}

	@Override
	protected String valueOfRelationship() {
		return "(" + type + ", " + leftFeatureId + ", " + rightFeatureId + ")";
	}
}
