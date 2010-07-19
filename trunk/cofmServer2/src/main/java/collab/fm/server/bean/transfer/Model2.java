package collab.fm.server.bean.transfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model2 extends Entity2 {
	private List<Attribute2> attrs = new ArrayList<Attribute2>();
	private Set<Long> users = new HashSet<Long>();
	
	public void addAttr(Attribute2 a2) {
		attrs.add(a2);
	}
	public List<Attribute2> getAttrs() {
		return attrs;
	}
	public void setAttrs(List<Attribute2> attrs) {
		this.attrs = attrs;
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
