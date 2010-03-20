package collab.fm.server.bean.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import collab.fm.server.bean.transfer.User2;

public class User {
	
	private int version;
	
	private Long id;
	private String name;
	private String password;
	
	private Set<Model> models = new HashSet<Model>();
	
	public User() {
		
	}
	
	public User2 transfer() {
		User2 u = new User2();
		u.setId(this.getId());
		u.setName(this.getName());
		
		return u;
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
	
	public Long getId() {
		return id;
	}
	private void setId(Long id) {
		this.id = id;
	}
	
	public void addModel(Model model) {
		this.getModelsInternal().add(model);
		model.addUser(this);
	}
	
	public Set<Model> getModels() {
		return Collections.unmodifiableSet(getModelsInternal());
	}
	
	private Set<Model> getModelsInternal() {
		return models;
	}

	private void setModelsInternal(Set<Model> models) {
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
