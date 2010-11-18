package collab.fm.server.bean.persist;

public class Preference extends DataItem {

	private Model model;
	private String content;
	
	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		String m = "";
		if (this.getModel() != null) {
			m = this.getModel().toValueString();
		}
		return m + content;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
