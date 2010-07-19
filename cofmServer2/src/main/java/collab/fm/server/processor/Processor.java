package collab.fm.server.processor;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.*;

public interface Processor {
	
	/**
	 * 
	 * @param req
	 * @param rg
	 * @return true if processing has succeed.
	 */
	public boolean process(Request req, ResponseGroup rg) 
		throws EntityPersistenceException, StaleDataException, InvalidOperationException;
	
	public boolean checkRequest(Request req);
}
