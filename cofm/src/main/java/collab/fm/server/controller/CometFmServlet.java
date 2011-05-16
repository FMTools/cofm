package collab.fm.server.controller;

import java.io.*;

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

	private class FmHandler implements CometHandler<HttpServletResponse> {

		private HttpServletResponse response;
		private int clientId;
		
		public String toString() {
			return "Client #" + clientId;
		}
		
		public void onEvent(CometEvent event) throws IOException {
			if (CometEvent.NOTIFY == event.getType()) {
				PrintWriter writer = response.getWriter();
				ResponseGroup fmRes = (ResponseGroup) event.attachment();
				String msg = null;
				// Case 1: Send back response to its requester
				if (fmRes.getBack() != null && fmRes.getBack().getRequestClientId() == clientId) {
					msg = fmRes.getJsonBack();
				} else if (fmRes.getBroadcast() != null) {
					msg = fmRes.getJsonBroadcast();
				}
				if (msg != null) {
					writer.write("<script type='text/javascript'>parent.onResponseArrived('" 
							+ msg + "');</script>\n");   
					writer.flush();
					
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
		FmHandler handler = new FmHandler();
		handler.attach(res);
		handler.forClient(Integer.valueOf(cId));

		logger.debug("GET from client #" + cId);
		
		CometEngine engine = CometEngine.getEngine();
		CometContext context = engine.getCometContext(contextPath);

		context.addCometHandler(handler);
		
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
				res.getWriter().write("__handshake__" + (nextClientId++));
				logger.info("Handshaking with client #" + (nextClientId-1));
				return;
			} else if (mode.equals(MODE_QUIT)) {
				if (cId == null) {
					return;
				}
				fmRes = Controller.instance().disconnectUser(Integer.valueOf(cId));
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
		
		CometEngine engine = CometEngine.getEngine();
		CometContext<?> context = engine.getCometContext(contextPath);
		
		logger.debug("Handlers in Context: " + context.getCometHandlers().toString());
		
		context.notify(fmRes);
	}
}
