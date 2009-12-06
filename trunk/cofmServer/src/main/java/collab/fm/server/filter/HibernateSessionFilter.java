package collab.fm.server.filter;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.FilterException;

public class HibernateSessionFilter extends Filter {

	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
				return false;
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
				return false;
		// TODO: Open a new session here.

	}
}
