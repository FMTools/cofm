package collab.fm.server.bean.persist;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.User2;
import collab.fm.server.util.DataItemUtil;

public class User extends DataItem {
	
	private Date lastLoginTime;
	
	private String name;
	private String password;
	private String email;
	
	private Boolean validated;
	private String validationStr;
	
	private Set<Model> models = new HashSet<Model>();

	private Set<Preference> preferences = new HashSet<Preference>();
	
	@Override
	public void transfer(DataItem2 u) {
		User2 u2 = (User2) u;
		u2.setId(this.getId());
		u2.setName(this.getName());
		if (this.getLastLoginTime() != null) {
			u2.setLastLoginTime(DataItemUtil.formatDate(this.getLastLoginTime()));
		} else {
			u2.setLastLoginTime(null);
		}
	}
	
	@Override
	public String toValueString() {
		if (getId() != null) {
			return getId().toString();
		}
		return getName();
	}
	
	public void addModel(Model model) {
		this.getModels().add(model);
		model.addUser(this);
	}
	
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Set<Model> getModels() {
		return models;
	}
	
	public void setModels(Set<Model> models) {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Preference> getPreferences() {
		return preferences;
	}

	public void setPreferences(Set<Preference> preferences) {
		this.preferences = preferences;
	}

	public Boolean isValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public String getValidationStr() {
		return validationStr;
	}

	public void setValidationStr(String validationStr) {
		this.validationStr = validationStr;
	}

}
