package collab.fm.server.bean.entity;

public class User {
	private Long id;
	
	private String name;
	
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
}
