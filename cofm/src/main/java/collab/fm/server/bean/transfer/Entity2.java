package collab.fm.server.bean.transfer;

import java.util.ArrayList;
import java.util.List;

public class Entity2 extends VotableElement2 {
	private Long model;
	private List<ValueList2> attrs = new ArrayList<ValueList2>();
	private List<Comment2> comments = new ArrayList<Comment2>();
	
	public Long getModel() {
		return model;
	}
	public void setModel(Long model) {
		this.model = model;
	}
	public List<ValueList2> getAttrs() {
		return attrs;
	}
	public void setAttrs(List<ValueList2> attrs) {
		this.attrs = attrs;
	}
	public List<Comment2> getComments() {
		return comments;
	}
	public void setComments(List<Comment2> comments) {
		this.comments = comments;
	}
}
