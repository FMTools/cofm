package collab.fm.server.bean.protocol;

public class EditFeatureRequest extends Request {
	private Long featureId;

	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}
	
	public boolean valid() {
		if (super.valid()) {
			return featureId != null;
		}
		return false;
	}
	
}
