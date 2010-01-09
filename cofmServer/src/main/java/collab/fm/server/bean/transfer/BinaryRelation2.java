package collab.fm.server.bean.transfer;

import java.util.Set;

public class BinaryRelation2 {
	private Long id;
	private String type;
	private Set<Long> v1;
	private Set<Long> v0;
	private Long left;
	private Long right;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Set<Long> getV1() {
		return v1;
	}
	public void setV1(Set<Long> v1) {
		this.v1 = v1;
	}
	public Set<Long> getV0() {
		return v0;
	}
	public void setV0(Set<Long> v0) {
		this.v0 = v0;
	}
	public Long getLeft() {
		return left;
	}
	public void setLeft(Long left) {
		this.left = left;
	}
	public Long getRight() {
		return right;
	}
	public void setRight(Long right) {
		this.right = right;
	}
	
	
}
