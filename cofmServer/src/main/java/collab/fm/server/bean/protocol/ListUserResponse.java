package collab.fm.server.bean.protocol;

import java.util.List;

public class ListUserResponse extends Response {
	
	private List<User2> users;
	
	public List<User2> getUsers() {
		return users;
	}

	public void setUsers(List<User2> users) {
		this.users = users;
	}

	public static class User2 {
		private Long id;
		private String name;
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
		
	}
}
