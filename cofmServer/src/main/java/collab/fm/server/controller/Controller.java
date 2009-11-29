package collab.fm.server.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.filter.*;
import collab.fm.server.action.Action;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.exception.JsonConvertException;

public abstract class Controller {
	
	public static final String BAD_REQUEST = "bad_request";
	public static final String BAD_RESPONSE = "bad_response";
	
	protected List<Filter> filterChain = new ArrayList<Filter>();
	
	protected ConcurrentHashMap<String, Action> eventMap = new ConcurrentHashMap<String, Action>();
	
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
			try {
			    getLogger().debug("Forwarded to doBadRequest: " + BeanUtil.beanToJson(request));
			} catch (JsonConvertException e) {
				//ignore it
			}
			rawResponse = doBadRequest(request);
		} else {
			try {
				getLogger().debug("Forwarded to doRequest: " + BeanUtil.beanToJson(filteredRequest));
			} catch (JsonConvertException e) {
				//ignore
			}
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
