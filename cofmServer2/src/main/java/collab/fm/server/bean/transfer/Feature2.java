package collab.fm.server.bean.transfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Feature2 extends VotableEntity2 {
	private Long model;
	private List<Attribute2> attrs = new ArrayList<Attribute2>();
	private List<Comment2> comments = new ArrayList<Comment2>();
	private Set<Long> rels = new HashSet<Long>();
	
	
	public Long getModel() {
		return model;
	}
	public void setModel(Long model) {
		this.model = model;
	}
	public void addAttr(Attribute2 a) {
		this.attrs.add(a);
	}
	public List<Attribute2> getAttrs() {
		return attrs;
	}
	public void setAttrs(List<Attribute2> attrs) {
		this.attrs = attrs;
	}
	public void addComment(Comment2 c) {
		this.comments.add(c);
	}
	public List<Comment2> getComments() {
		return comments;
	}
	public void setComments(List<Comment2> comments) {
		this.comments = comments;
	}
	public void addRel(Long r) {
		this.rels.add(r);
	}
	public Set<Long> getRels() {
		return rels;
	}
	public void setRels(Set<Long> rels) {
		this.rels = rels;
	}
	
}
