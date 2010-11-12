package collab.fm.server.bean.transfer;

import java.util.HashSet;
import java.util.Set;

public class Model2 extends DataItem2 {
	private String name;
	private String des;
	private Set<Long> users = new HashSet<Long>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public void addUser(Long u) {
		users.add(u);
	}
	public Set<Long> getUsers() {
		return users;
	}
	public void setUsers(Set<Long> users) {
		this.users = users;
	}
	
}
