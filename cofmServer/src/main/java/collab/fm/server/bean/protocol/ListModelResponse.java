package collab.fm.server.bean.protocol;

import java.util.List;
import java.util.Set;

import collab.fm.server.bean.protocol.UpdateResponse.Des2;
import collab.fm.server.bean.protocol.UpdateResponse.Name2;

public class ListModelResponse extends Response {
	
	private List<Model2> models;
	
	public List<Model2> getModels() {
		return models;
	}

	public void setModels(List<Model2> models) {
		this.models = models;
	}

	public static class Model2 {
		private List<Name2> name;
		private List<Des2> des;
		private Long id;
		private Set<Long> user;
		
		public List<Name2> getName() {
			return name;
		}
		public void setName(List<Name2> name) {
			this.name = name;
		}
		public List<Des2> getDes() {
			return des;
		}
		public void setDes(List<Des2> des) {
			this.des = des;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Set<Long> getUser() {
			return user;
		}
		public void setUser(Set<Long> user) {
			this.user = user;
		}
	}
}
