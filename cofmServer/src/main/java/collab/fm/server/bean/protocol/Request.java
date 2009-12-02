package collab.fm.server.bean.protocol;

public class Request {
	public static final String TERMINATOR = "\n";
	
	protected Long id;
	protected String name;
	protected String address;
	protected Long requesterId; // The user id of the requester
	
	public Request() {
		
	}

	public boolean valid() {
		// The requester maybe unaware of its IP address.
		return id != null && name != null && requesterId != null; 
	}
	
	public String toString() {
		return id + " " + name + " " + address + " " + requesterId;
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
	
}
