package collab.fm.server.bean.protocol;

import org.apache.log4j.Logger;

public class CommitRequest extends Request {
	
	static Logger logger = Logger.getLogger(CommitRequest.class);
	
	private Operation operation;

	public String toString() {
		return super.toString() + " " + operation.toString();
	}
	
	public boolean valid() {
		logger.debug("check CommitRequest is valid.");
		
		if (super.valid()) {
			return operation != null && operation.valid();
		}
		return false;
	}
	
	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
}
