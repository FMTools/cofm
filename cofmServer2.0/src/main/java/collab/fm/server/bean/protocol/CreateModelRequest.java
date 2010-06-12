package collab.fm.server.bean.protocol;

public class CreateModelRequest extends Request {
	private String modelName;
	private String description;

	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
