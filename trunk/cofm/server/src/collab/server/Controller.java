package collab.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.data.Request;
import collab.data.Response;
import collab.filter.Filter;
import collab.util.Utils;
import collab.action.Action;

public abstract class Controller {
	
	public static final String BAD_REQUEST = "bad_request";
	public static final String BAD_RESPONSE = "bad_response";
	
	protected List<Filter> filterChain = new ArrayList<Filter>();
	
	protected ConcurrentHashMap<String, Action> eventMap = new ConcurrentHashMap<String, Action>();
	
	public Controller() {
	}
	
	public void registerAction(String event, Action a) {
		if (isInterestedEvent(event)) {
			eventMap.put(event, a);
		}
	}
	
	public void addFilter(Filter filter) {
		filterChain.add(filter);
	}
	
	public List<Response> handleRequest(Request request) {
		Request filteredRequest = null;
		List<Response> rawResponse = null;
		
		for (ListIterator<Filter> li = filterChain.listIterator(0); li.hasNext();) {
			Filter filter = li.next();
			if ((filteredRequest = filter.filterRequest(request)) == null) {
				break;
			}
			request = filteredRequest;
		}
		
		if (filteredRequest == null) {
			getLogger().info("Forwarded to doBadRequest: " + Utils.beanToJson(request));
			rawResponse = doBadRequest(request);
		} else {
			getLogger().info("Forwarded to doRequest: " + Utils.beanToJson(filteredRequest));
			rawResponse = doRequest(filteredRequest);
		}
		
		if (rawResponse == null) {
			return null;
		}
		
		List<Response> filteredResponse = new LinkedList<Response>();
		for (Response rsp: rawResponse) {
			Response frsp = null;
			for (ListIterator<Filter> li = filterChain.listIterator(filterChain.size()); li.hasPrevious();) {
				Filter filter = li.previous();
				if ((frsp = filter.filterResponse(rsp)) == null) {
					break;
				}
				rsp = frsp;
			}
			if (frsp == null) {
				doBadResponse(rsp);
			} else {
				filteredResponse.add(frsp);
			}
		}
		
		
		return filteredResponse;
	}
	
	protected abstract boolean isInterestedEvent(String name);
	protected abstract List<Response> doBadRequest(Request req);
	protected abstract List<Response> doRequest(Request req);
	protected abstract void doBadResponse(Response rsp);
	protected abstract Logger getLogger();
}
