package collab.filter;

import collab.data.*;

public abstract class Filter {
	protected Filter prev;
	protected Filter next;   // TODO: should prev & next be final?
	protected final String name;
	
	public Filter(String filterName, Filter prevFilter, Filter nextFilter) {
		prev = prevFilter;
		next = nextFilter;
		name = filterName;
	}
	
	public Request filterRequest(Request request) {
		request.latestFilter(name);
		Request filteredRequest = doFilterRequest(request);
		if (filteredRequest != null) {
			if (next != null) {
				return next.filterRequest(filteredRequest);
			}
		}
		return filteredRequest;
	}
	
	public Response filterResponse(Response response) {
		response.latestFilter(name);
		Response filteredResponse = doFilterResponse(response);
		if (filteredResponse != null) {
			if (prev != null) {
				return prev.filterResponse(response);
			}
		}
		return filteredResponse;
	}
	
	protected void onFilterError(Filterable filtee, String error, String msg) {
		filtee.filterError(error);
		filtee.filterMessage(msg);
	}
	
	protected abstract Request doFilterRequest(Request request);
	protected abstract Response doFilterResponse(Response response);
	
	public String getName() {
		return name;
	}
}
