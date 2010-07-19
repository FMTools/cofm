package collab.fm.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.action.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.filter.*;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.JsonConvertException;

public class Controller {
	
	static Logger logger = Logger.getLogger(Controller.class);
	
	private static final String[] databaseAccessRequests = {
		Resources.REQ_COMMENT,
		Resources.REQ_LOGIN,
		Resources.REQ_REGISTER,
		Resources.REQ_UPDATE,
		Resources.REQ_CREATE_MODEL,
		Resources.REQ_LIST_MODEL,
		Resources.REQ_LIST_USER
	};
	
	private static Controller controller = new Controller();
	
	private Filter accessValidator = new AccessValidator();
	private Filter requestHandler = new RequestHandler();
	
	public static Controller instance() {
		return controller;
	}
	
	public static void init() {
		logger.info("Controller initialized.");
	}
	
	private Controller() {

	}
	
	public ResponseGroup disconnectUser(String addr) {
		ResponseGroup rg = new ResponseGroup();
		rg.setBack(null);
		rg.setBroadcast(null);
		rg.setPeer(null);
		
		FilterChain chain = buildChainForDisconnection();
		chain.doDisconnectUser(addr, rg);
		
		try {
			convertResponsesToJson(rg);
		} catch (Exception ex) {
			logger.error("Internal error: couldn't convert responses to JSON.", ex);
			return null;
		}
		
		return rg;
	}
	
	private FilterChain buildChainForDisconnection() {
		FilterChain fc = new FilterChain();
		fc.addFilter(accessValidator);
		return fc;
	}
	
	public ResponseGroup execute(String message, String sourceAddress) {
		logger.info("--- Request is: '" + message + "'");
		Request req = null;
		ResponseGroup rg = new ResponseGroup();
		rg.setBack(null);
		rg.setBroadcast(null);
		rg.setPeer(null);
		try {
			// 1. Build request from message (a JSON string actually)
			req = JsonConverter.jsonToRequest(message);
			req.setAddress(sourceAddress);
			req.setLastError(null);
			
			// 2. Build a chain to process the request
			FilterChain chain = buildChain(req.getName());
			
			logger.info("Filter chain is starting...");
			chain.doNextFilter(req, rg);
			
			if (req.getLastError() != null) {
				logger.info("Got error when filtering request: " + req.getLastError());
				reportFilterError(Resources.RSP_ERROR, req, rg);
			}
			
		} catch (Exception e) {
			if (req.getLastError() == null) {
				req.setLastError("Internal error occured.");
			}
			logger.debug("Exception raised.", e);
			reportFilterError(Resources.RSP_SERVER_ERROR, req, rg);
		}
		
		try {
			convertResponsesToJson(rg);
		} catch (Exception ex) {
			logger.error("Internal error: couldn't convert responses to JSON.", ex);
			return null;
		}
		logger.info("Responses are ready: " + rg.toString());
		return rg;
	}
	
	private FilterChain buildChain(String requestName) {
		FilterChain chain = new FilterChain();
		chain.addFilter(accessValidator);
		if (needDatabaseAccess(requestName)) {
			// Session per request
			chain.addFilter(new HibernateSessionFilter());
		}
		chain.addFilter(requestHandler);
		return chain;
	}
	
	private boolean needDatabaseAccess(String requestName) {
		for (String name: databaseAccessRequests) {
			if (name.equals(requestName)) {
				return true;
			}
		}
		return false;
	}
	
	private void reportFilterError(String errorCode, Request req, ResponseGroup rg) {
		// Disallow multicast and broadcast
		rg.setBroadcast(null);
		rg.setPeer(null);
		rg.setTargets(null);
		
		Response rsp = new Response();
		rsp.setMessage(req.getLastError());
		rsp.setName(errorCode);
		if (req != null) {
			rsp.setRequesterId(req.getRequesterId());
			rsp.setRequestId(req.getId());
			rsp.setRequestName(req.getName());
		}
		rg.setBack(rsp);
		
		logger.warn(errorCode + " " + req.getLastError());
	}
	
	private void convertResponsesToJson(ResponseGroup rg) throws JsonConvertException {
		rg.setJsonBack(JsonConverter.responseToJson(rg.getBack()));
		rg.setJsonBroadcast(JsonConverter.responseToJson(rg.getBroadcast()));
		rg.setJsonPeer(JsonConverter.responseToJson(rg.getPeer()));
	}
	
}
