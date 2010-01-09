package collab.fm.server.bean.transfer;

import java.util.List;
import java.util.Set;

public class Model2 {
	private Long id;
	private List<VotableString> names;
	private List<VotableString> dscs;
	private Set<Long> users;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<VotableString> getNames() {
		return names;
	}
	public void setNames(List<VotableString> names) {
		this.names = names;
	}
	public List<VotableString> getDscs() {
		return dscs;
	}
	public void setDscs(List<VotableString> dscs) {
		this.dscs = dscs;
	}
	public Set<Long> getUsers() {
		return users;
	}
	public void setUsers(Set<Long> users) {
		this.users = users;
	}
	
}
