package collab.fm.server.bean.protocol;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.processor.Processor;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class Request {
	public static final String TERMINATOR = "\0";
	
	protected Long id;
	protected String name;
	protected int clientId;  // The id of the flex client that sends the request
	protected Long requesterId; // The user id of the requester
	
	protected String lastError = null; 

	protected List<Processor> processors = new ArrayList<Processor>();
	
	public Request() {
		processors.add(makeDefaultProcessor());
	}
	
	/**
	 * Sub-classes of Request should override this method.
	 */
	protected Processor makeDefaultProcessor() {
		return new SimpleBroadcastingProcessor();
	}
	
	public boolean process(ResponseGroup rg) 
	throws ItemPersistenceException, StaleDataException, InvalidOperationException {
		for (Processor p: processors) {
			if (p.process(this, rg) == false) {
				return false;
			}
		}
		return true;
	}
	
	public void addProcessor(Processor p) {
		processors.add(p);
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	public boolean valid() {
		// The requester maybe unaware of its IP address and/or requester ID (e.g. a register request).
		// But these fields might be checked by subclasses.
		return name != null; 
	}
	
	public String toString() {
		return clientId + " " + id + " " + name + " " + requesterId;
	}
	
	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	protected static class SimpleBroadcastingProcessor implements Processor {
		/**
		 * Do nothing special, just "fill-response" and broadcast the response to each client (including
		 * the requester.)
		 * @throws InvalidOperationException 
		 */
		public boolean process(Request req, ResponseGroup rg) throws InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid operation.");
			}
			Response r = fillResponse(req);
			r.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(r);
			
			Response back = fillResponse(req);
			back.setName(Resources.RSP_SUCCESS);
			rg.setBack(back);
			
			return true;
		}
		
		/**
		 * Sub-classes of this processor should override the fillResponse method.
		 */
		protected Response fillResponse(Request req) {
			return new Response(req);
		}
		
		public boolean checkRequest(Request req) {
			// do nothing here
			return true;
		}
	}
	
}
