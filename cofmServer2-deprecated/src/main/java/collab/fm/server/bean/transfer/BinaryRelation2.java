package collab.fm.server.bean.transfer;


public class BinaryRelation2 extends VotableEntity2 {
	private String type;
	private Long left;
	private Long right;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
