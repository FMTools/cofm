package collab.server;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

import collab.data.Request;
import collab.data.Response;
import collab.filter.Filter;
import collab.action.Action;

public abstract class Controller {
	
	public static final String BAD_REQUEST = "bad_request";
	public static final String BAD_RESPONSE = "bad_response";
	
	protected List<Filter> filterChain = new ArrayList<Filter>();
	
	protected ConcurrentHashMap<String, Action> eventMap = new ConcurrentHashMap<String, Action>();
	
	public Controller() {
		buildFilterChain();
	}
	
	public void registerAction(String event, Action a) {
		if (isInterestedEvent(event)) {
			eventMap.put(event, a);
		}
	}
	
	public Response handleRequest(Request request) {
		Request filteredRequest = null;
		Response rawResponse = null;
		Response filteredResponse = null;
		
		for (ListIterator<Filter> li = filterChain.listIterator(0); li.hasNext();) {
			Filter filter = li.next();
			if ((filteredRequest = filter.filterRequest(request)) == null) {
				break;
			}
			request = filteredRequest;
		}
		
		if (filteredRequest == null) {
			rawResponse = doBadRequest(request);
		} else {
			rawResponse = doRequest(filteredRequest);
		}
		
		if (rawResponse == null) {
			return null;
		}
		
		for (ListIterator<Filter> li = filterChain.listIterator(filterChain.size()); li.hasPrevious();) {
			Filter filter = li.previous();
			if ((filteredResponse = filter.filterResponse(rawResponse)) == null) {
				break;
			}
			rawResponse = filteredResponse;
		}
		
		if (filteredResponse == null) {
			filteredResponse = doBadResponse(rawResponse);
		}
		return filteredResponse;
	}
	
	protected abstract void buildFilterChain();
	protected abstract boolean isInterestedEvent(String name);
	protected abstract Response doBadRequest(Request req);
	protected abstract Response doRequest(Request req);
	protected abstract Response doBadResponse(Response rsp);
}
