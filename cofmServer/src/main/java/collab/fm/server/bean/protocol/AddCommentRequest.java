package collab.fm.server.bean.protocol;

public class AddCommentRequest extends Request {
	private Long featureId;
	private String content;
	public Long getFeatureId() {
		return featureId;
	}
	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
