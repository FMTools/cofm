package collab.fm.server.bean.entity;

import collab.fm.server.bean.transfer.Comment2;

public class Comment extends VersionedEntity {
	private Long id;
	private Long featureId;
	private String content;
	
	public Comment() {
		
	}
	
	public Comment(Long creator) {
		super(creator);
	}
	
	public Comment2 transfer() {
		Comment2 rslt = new Comment2();
		rslt.setCid(this.getCreator());
		rslt.setContent(this.getContent());
		rslt.setTime(this.getCreated().toString());
		return rslt;
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
