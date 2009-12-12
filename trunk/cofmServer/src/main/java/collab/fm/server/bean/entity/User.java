package collab.fm.server.bean.entity;

public class User {
	
	private int version;
	
	private Long id;
	private String name;
	private String password;
	
	public User() {
		
	}
	
	public Long getId() {
		return id;
	}
	private void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
