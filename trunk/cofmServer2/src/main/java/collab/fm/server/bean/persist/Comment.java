package collab.fm.server.bean.persist;

import collab.fm.server.bean.transfer.DataItem2;

public class Comment extends DataItem {
	private String content;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public void transfer(DataItem2 c) {
//		Comment2 c2 = (Comment2) c;
//		super.transfer(c2);
//		c2.setContent(this.getContent());
	}
	
	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return this.content;
	}
}
