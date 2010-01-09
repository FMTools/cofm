package collab.fm.server.bean.protocol;

import java.util.List;

import collab.fm.server.bean.transfer.Model2;

public class ListModelResponse extends Response {
	
	private List<Model2> models;
	
	public List<Model2> getModels() {
		return models;
	}

	public void setModels(List<Model2> models) {
		this.models = models;
	}
}
