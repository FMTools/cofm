package collab.data.bean;

public class Operation {
	private Long id;
	private String op;
	private String left;
	private String right;  // op(left, right);
	private boolean vote;
	private String committer;
	
	public Operation() {
		
	}
	
	public Long getId() {
		return id;
	}
	
	private void setId(Long id) { // for Hibernate
		this.id = id;
	}
	
	public String getOp() {
		return op;
	}
	
	public void setOp(String op) {
		this.op = op;
	}

	public String getLeft() {
		return left;
	}
	
	public void setLeft(String left) {
		this.left = left;
	}
	
	public String getRight() {
		return right;
	}
	
	public void setRight(String right) {
		this.right = right;
	}
	
	public String getCommitter() {
		return committer;
	}
	
	public void setCommitter(String committer) {
		this.committer = committer;
	}
}
