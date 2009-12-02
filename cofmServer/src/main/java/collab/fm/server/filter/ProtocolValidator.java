package collab.fm.server.filter;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.FilterException;

public class ProtocolValidator extends Filter {

	private void writeSource(Request req, Response rsp) {
		if (rsp != null) {
			rsp.setRequesterId(req.getRequesterId());
			rsp.setRequestId(req.getId());
			rsp.setRequestName(req.getName());
		}
	}
	
	@Override
	protected void doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// Ensure the source information has been added to responses
		writeSource(req, rg.getBack());
		writeSource(req, rg.getBroadcast());
		writeSource(req, rg.getPeer());
	}

	@Override
	protected void doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// do nothing now
		
	}

	@Override
	protected FilterException onFilterError(Request req, ResponseGroup rg,
			Throwable t) {
		// TODO Auto-generated method stub
		return null;
	}

}
