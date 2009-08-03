package collab.filter;

public class Filterable {
	protected String latestFilterName;
	protected String latestFilterMsg;
	
	public void filterMessage(String message) {
		latestFilterMsg = message;
	}

	public String filterMessage() {
		return latestFilterMsg;
	}

	public void latestFilter(String name) {
		latestFilterName = name;
	}

	public String latestFilter() {
		return latestFilterName;
	}
}
