package collab.fm.server.bean;

public abstract class Filterable {
	protected String latestFilterName;
	protected String filterMsg;
	protected String filterError;
	
	public void filterMessage(String message) {
		filterMsg = message;
	}

	public String filterMessage() {
		return filterMsg;
	}

	public void latestFilter(String name) {
		latestFilterName = name;
	}

	public String latestFilter() {
		return latestFilterName;
	}
	
	public void filterError(String error) {
		filterError = error;
	}
	
	public String filterError() {
		return filterError;
	}
}
