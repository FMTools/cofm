package collab.fm.server.bean;

import java.util.List;

public class BinaryRelationshipOperation extends Operation {

	private Long leftFeatureId;
	private Long rightFeatureId;
	
	public BinaryRelationshipOperation() {
		
	}

	public List<Operation> apply() throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
		return super.toString() + " " + leftFeatureId + " " + rightFeatureId;
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
}
