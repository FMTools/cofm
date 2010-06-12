package collab.fm.server.filter;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.FilterException;

public abstract class Filter {
	
	public void doFilter(Request req, ResponseGroup rg, FilterChain chain) throws FilterException {
			if (!doForwardFilter(req, rg)) {
				if (req.getLastError() == null) {
					// Ensure the last error is set.
					req.setLastError("Got error in " + this.getClass().toString());
				}
				return;
			}
			
			chain.doNextFilter(req, rg);
			
			if (!doBackwardFilter(req, rg)) {
				if (req.getLastError() == null) {
					// Ensure the last error is set.
					req.setLastError("Got error in " + this.getClass().toString());
				}
				return;
			}
	}
	
	public void doDisconnectUser(String address, ResponseGroup rg, FilterChain chain) {
		this.doDisconnection(address, rg);
		
		chain.doDisconnectUser(address, rg);
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
	
	protected abstract void doDisconnection(String addr, ResponseGroup rg);
}
