package collab.filter;

import org.apache.log4j.Logger;

import collab.data.*;

public abstract class Filter {
	protected final String name;
	
	public Filter(String filterName) {
		name = filterName;
	}
	
	public Request filterRequest(Request request) {
		request.latestFilter(name);
		return doFilterRequest(request);
	}
	
	public Response filterResponse(Response response) {
		response.latestFilter(name);
		return doFilterResponse(response);
	}
	
	protected void onFilterError(Filterable filtee, String error, String msg) {
		getLogger().info("Filter failure(" + error + "): " + msg);
		filtee.filterError(error);
		filtee.filterMessage(msg);
	}
	
	protected void onFilterError(Filterable filtee, String error, String msg, Throwable t) {
		getLogger().info("Filter failure(" + error + "): " + msg, t);
		filtee.filterError(error);
		filtee.filterMessage(msg);
	}
	
	protected abstract Request doFilterRequest(Request request);
	protected abstract Response doFilterResponse(Response response);
	protected abstract Logger getLogger();
	
	public String getName() {
		return name;
	}
}
