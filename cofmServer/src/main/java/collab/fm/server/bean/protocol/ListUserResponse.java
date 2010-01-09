package collab.fm.server.bean.protocol;

import java.util.List;

import collab.fm.server.bean.transfer.User2;

public class ListUserResponse extends Response {
	
	private List<User2> users;
	
	public List<User2> getUsers() {
		return users;
	}

	public void setUsers(List<User2> users) {
		this.users = users;
	}
}
