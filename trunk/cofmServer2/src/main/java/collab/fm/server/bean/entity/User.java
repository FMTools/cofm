package collab.fm.server.bean.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import collab.fm.server.bean.transfer.User2;

public class User extends Entity {
	
	private String name;
	private String password;
	
	private Set<Model> models = new HashSet<Model>();
	
	public User() {
		super();
	}
	
	public void transfer(User2 u2) {
		u2.setId(this.getId());
		u2.setName(this.getName());
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof User)) return false;
		final User that = (User) v;
		if (getId() != null) {
			return getId().equals(that.getId());
		}
		return getName().equals(that.getName());
	}
	
	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		}
		return getName().hashCode();
	}
	
	public void addModel(Model model) {
		this.getModels().add(model);
		model.addUser(this);
	}
	
	public Set<Model> getModels() {
		return models;
	}
	
	private void setModels(Set<Model> models) {
		this.models = models;
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
