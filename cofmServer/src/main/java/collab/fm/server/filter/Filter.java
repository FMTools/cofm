package collab.fm.server.filter;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.FilterException;

public abstract class Filter {
	
	public void doFilter(Request req, ResponseGroup rg, FilterChain chain) throws FilterException {
		try {
			doForwardFilter(req, rg);
			chain.doNextFilter(req, rg);
			doBackwardFilter(req, rg);
		} catch (FilterException fe) {
			FilterException details = onFilterError(req, rg, fe);
			if (details == null) details = fe;
			throw details;
		}
	}
	
	protected abstract void doForwardFilter(Request req, ResponseGroup rg) throws FilterException;
	protected abstract void doBackwardFilter(Request req, ResponseGroup rg) throws FilterException;
	protected abstract FilterException onFilterError(Request req, ResponseGroup rg, Throwable t);	
}
