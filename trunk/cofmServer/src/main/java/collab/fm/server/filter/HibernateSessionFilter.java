package collab.fm.server.filter;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.FilterException;

public class HibernateSessionFilter extends Filter {

	@Override
	protected void doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub

	}

	@Override
	protected FilterException onFilterError(Request req, ResponseGroup rg,
			Throwable t) {
		// TODO Auto-generated method stub
		return null;
	}

}
