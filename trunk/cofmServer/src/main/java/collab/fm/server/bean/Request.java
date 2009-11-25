package collab.fm.server.bean;


public class Request extends Filterable {
	public static final String TERMINATOR = "\n";
	
	private Long id;
	private String name;
	private String address;
	private String user;
	
	/**
	 * Before the request is converted from JSON to Request, the JSON string is 
	 * stored in this field.
	 */
	private Object data;
	
	public Request() {
		
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
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}	
	
}
