package collab.fm.server.bean.entity;

public class Comment extends VersionedEntity {
	private Long id;
	private Long featureId;
	private String content;
	
	public Comment() {
		
	}
	
	public Comment(Long creator) {
		super(creator);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
