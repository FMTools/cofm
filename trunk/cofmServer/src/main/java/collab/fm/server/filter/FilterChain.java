package collab.fm.server.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.FilterException;

public class FilterChain {

	private List<Filter> chain;
	private Iterator<Filter> itr;
	
	public FilterChain() {
		chain = new ArrayList<Filter>();
	}
	
	public void addFilter(Filter f) {
		chain.add(f);
	}
	
	public void doNextFilter(Request req, ResponseGroup rg) throws FilterException {
		if (itr == null) {
			itr = chain.iterator();
		} 
		if (itr.hasNext()) {
			itr.next().doFilter(req, rg, this);
		}
		
	}
}
