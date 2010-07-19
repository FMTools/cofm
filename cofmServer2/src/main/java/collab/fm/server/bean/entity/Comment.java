package collab.fm.server.bean.entity;

import collab.fm.server.bean.transfer.Comment2;

public class Comment extends Entity {
	private String content;
	
	public Comment() {
		
	}
	
	public Comment(Long creator) {
		super(creator);
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public void transfer(Comment2 c2) {
		super.transfer(c2);
		c2.setContent(this.getContent());
	}
}
