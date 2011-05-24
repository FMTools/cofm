package collab.fm.server.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.apache.commons.lang.*;

import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.protocol.ResponseGroup;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import com.sun.grizzly.comet.CometEvent;
import com.sun.grizzly.comet.CometHandler;

public class CometFmServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(CometFmServlet.class);
	
	private static final String MODE_HANDSHAKE = "handshake";
	private static final String MODE_QUIT = "quit";
	
	private static final long serialVersionUID = -7923410507957675563L;

	private class ResponseBuffer {
		private List<String> res = new ArrayList<String>();
		private boolean active = true;
		private boolean closeAfterTransmission = false;
		
		private String toJsonString() {
			// return "[res1, res2, ...]"
			StringBuffer s = new StringBuffer();
			s.append("[");
			s.append(res.get(0));
			for (int i = 1; i < res.size(); i++) {
				s.append(",");
				s.append(res.get(i));
			}
			s.append("]");
			return s.toString();
		}
		
		public void flushToWriter(PrintWriter writer) {
			writer.write("<script type='text/javascript'>parent.onResponseArrived('" 
					+ this.toJsonString() + "');</script>\n");   
			writer.flush();
			
			if (this.isCloseAfterTransmission()) {
				this.setActive(false);
			}
			res.clear();
		}
		
		public int size() {
			return res.size();
		}
		
		public void appendResponse(String jsonRes) {
			res.add(jsonRes);
		}
		
		public boolean isEmpty() {
			return res.isEmpty();
		}
		
		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public boolean isCloseAfterTransmission() {
			return closeAfterTransmission;
		}

		public void setCloseAfterTransmission(boolean closeAfterTransmission) {
			this.closeAfterTransmission = closeAfterTransmission;
		}
		
	}
	
	private Map<Integer, ResponseBuffer> resBuffer = new HashMap<Integer, ResponseBuffer>();
	
	private class FmHandler implements CometHandler<HttpServletResponse> {

		private HttpServletResponse response;
		private int clientId;
		
		public String toString() {
			return "Client #" + clientId;
		}
		
		public void onEvent(CometEvent event) throws IOException {
			if (CometEvent.NOTIFY == event.getType()) {
				PrintWriter writer = response.getWriter();
				ResponseBuffer buf = resBuffer.get(Integer.valueOf(clientId));
				if (buf != null) {
					logger.debug("Write to " + this.toString());
					buf.flushToWriter(writer);
					
					event.getCometContext().resumeCometHandler(this);
				}
			}
		}

		public void onInitialize(CometEvent event) throws IOException {
		}

		public void onInterrupt(CometEvent event) throws IOException {
			removeThisFromContext();
		}

		public void onTerminate(CometEvent event) throws IOException {
			removeThisFromContext();
		}


		private void removeThisFromContext() throws IOException {
			response.getWriter().close();
			CometContext context =
				CometEngine.getEngine().getCometContext(contextPath);
			context.removeCometHandler(this);
			logger.debug("Remove handler: " + toString());
		}


		public void attach(HttpServletResponse attachment) {
			this.response = attachment;
		}
		
		public void forClient(int clientId) {
			this.clientId = clientId;
		}
	}

	private String contextPath = null;
	private int nextClientId = 1;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		Controller.init();
		
		ServletContext context = config.getServletContext();
		contextPath = context.getContextPath() + "/flexrpc";

		CometEngine engine = CometEngine.getEngine();
		CometContext cometContext = engine.register(contextPath);
		cometContext.setExpirationDelay(120 * 1000);
		
		logger.info("*** FM Servlet started.");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		String cId = req.getParameter("clientId");
		if (cId == null) {
			return;
		}
		
		ResponseBuffer buf = resBuffer.get(Integer.valueOf(cId));
		if (buf == null) {
			buf = new ResponseBuffer();
			resBuffer.put(Integer.valueOf(cId), buf);
		}
		
		if (buf.isEmpty()) {  
			// Wait for data (by creating a CometHandler for it)
			FmHandler handler = new FmHandler();
			handler.attach(res);
			handler.forClient(Integer.valueOf(cId));
	
			logger.debug("GET from client #" + cId + ", waiting for data...");
			
			CometEngine engine = CometEngine.getEngine();
			CometContext context = engine.getCometContext(contextPath);
	
			context.addCometHandler(handler);
		} else {
			// Write buffered data immediately
			logger.debug("GET from client #" + cId + ", " + buf.size() + " buffered responses.");
			buf.flushToWriter(res.getWriter());
		}
		
	}

	private String paramsToString(HttpServletRequest req) {
		String s = "{";
		for (String key: req.getParameterMap().keySet()) {
			s += key + "=";
			String[] vals = req.getParameterMap().get(key);
			if (vals.length == 0) {
				s += "null";
			} else {
				s += "[" + vals[0];
				for (int i = 1; i < vals.length; i++) {
					s += ", " + vals[i];
				}
				s += "]";
			}
			s += "; ";
		}
		s += "}";
		return s;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		logger.debug("POST param: " + paramsToString(req));
		ResponseGroup fmRes = null;
		String cId = req.getParameter("clientId");
		String mode = req.getParameter("mode");
		if (mode != null) {
			if (mode.equals(MODE_HANDSHAKE)) {
				res.setContentType("text/plain");
				res.getWriter().write("__handshake__" + nextClientId);
				
				resBuffer.put(nextClientId, new ResponseBuffer());
				
				logger.info("Handshaking with client #" + nextClientId);
				
				nextClientId++;
				return;
			} else if (mode.equals(MODE_QUIT)) {
				if (cId == null) {
					return;
				}
				fmRes = Controller.instance().disconnectUser(Integer.valueOf(cId));
				ResponseBuffer buf = resBuffer.get(Integer.valueOf(cId));
				if (buf != null) {
					buf.setCloseAfterTransmission(true);
				}
			}
		} else {
			if (cId == null) {
				// TODO: return Error state
				return;
			}
			String msg = req.getParameter("message");		
			fmRes = Controller.instance().execute(msg, Integer.valueOf(cId));
		}
		
		logger.debug("Response is: " + fmRes.toString());
		
		// Write the response (fmRes) into corresponding buffer(s), there are 2 cases:
		//   1. The response is sent to its requester. (if fmRes.getBack() != null)
		//   2. The response is broadcast to all clients EXCEPT its requester. (if fmRes.getBroadcast() != null)
		for (Map.Entry<Integer, ResponseBuffer> rb: resBuffer.entrySet()) {
			if (rb.getValue().isActive()) {
				if (fmRes.getBack() != null && 
						rb.getKey().equals(fmRes.getBack().getRequestClientId())) {
					rb.getValue().appendResponse(fmRes.getJsonBack());
				}
				if (fmRes.getBroadcast() != null &&
						!rb.getKey().equals(fmRes.getBroadcast().getRequestClientId())) {
					rb.getValue().appendResponse(fmRes.getJsonBroadcast());
				}
			} else {
				// Remove inactive buffers
				resBuffer.remove(rb.getKey());
			}
		}
		
		CometEngine engine = CometEngine.getEngine();
		CometContext<?> context = engine.getCometContext(contextPath);
		
		logger.debug("Ready clients: " + context.getCometHandlers().toString());
		
		context.notify(null);  
	}
}
