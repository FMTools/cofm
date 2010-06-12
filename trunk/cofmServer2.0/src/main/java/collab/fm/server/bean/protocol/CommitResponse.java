package collab.fm.server.bean.protocol;

import java.util.List;

import collab.fm.server.bean.operation.Operation;

public class CommitResponse extends Response {
	
	private List<Operation> operations;

	public boolean valid() {
		if (super.valid()) {
			return operations != null;
		}
		return false;
	}
	
	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}
	
	public void addOperation(Operation op) {
		operations.add(op);
	}
	
	public void addOperations(List<Operation> ops) {
		operations.addAll(ops);
	}
	
}
