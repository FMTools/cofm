package collab.fm.server.bean.protocol;

public class EditFeatureResponse extends Response {
	private Long featureId;
	private Long modelId;

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}
	
}
