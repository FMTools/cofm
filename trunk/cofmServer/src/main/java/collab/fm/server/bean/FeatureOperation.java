package collab.fm.server.bean;

import java.util.List;

public class FeatureOperation extends Operation {

	private Long featureId;
	private String value;
	
	public FeatureOperation() {
		
	}
	
	public List<Operation> apply() throws RuntimeException {
		return null;
		// TODO Auto-generated method stub

	}

	public String toString() {
		return super.toString() + " " + featureId + " " + value;
	}
	
	private Long getFeatureId() {
		return featureId;
	}

	private void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}

	private String getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}

}
