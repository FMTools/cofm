package collab.fm.server.bean.protocol;

public class Request {
	public static final String TERMINATOR = "\0";
	
	protected Long id;
	protected String name;
	protected String address;
	protected Long requesterId; // The user id of the requester
	protected Long modelId; // The id of current feature model
	
	protected String lastError = null; 

	public Request() {
		
	}

	public boolean valid() {
		// The requester maybe unaware of its IP address and/or requester ID (e.g. a register request).
		// But these fields might be checked by subclasses.
		return name != null; 
	}
	
	public String toString() {
		return id + " " + name + " " + address + " " + requesterId;
	}
	
	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	
}
