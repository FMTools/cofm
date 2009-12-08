package collab.fm.server.filter;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.FilterException;

public abstract class Filter {
	
	public void doFilter(Request req, ResponseGroup rg, FilterChain chain) throws FilterException {
		try {
			if (!doForwardFilter(req, rg)) {
				throw new FilterException("Forward-filter failed: ");
			}
			
			chain.doNextFilter(req, rg);
			
			if (!doBackwardFilter(req, rg)) {
				throw new FilterException("Backward-filter failed: ");
			}
		} catch (Exception e) {
			throw onError(req, rg, e);
		}
	}
	
	/**
	 * 
	 * @param req
	 * @param rg
	 * @return false if filter fails.
	 * @throws FilterException
	 */
	protected abstract boolean doForwardFilter(Request req, ResponseGroup rg) throws FilterException;
	
	/**
	 * 
	 * @param req
	 * @param rg
	 * @return false if filter fails.
	 * @throws FilterException
	 */
	protected abstract boolean doBackwardFilter(Request req, ResponseGroup rg) throws FilterException;
	
	protected FilterException onError(Request req, ResponseGroup rg, Exception e) {
		return new FilterException(e);
	}
}
