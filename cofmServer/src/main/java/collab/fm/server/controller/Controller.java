package collab.fm.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.action.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.filter.AccessValidator;
import collab.fm.server.filter.ActionDispatcher;
import collab.fm.server.filter.Filter;
import collab.fm.server.filter.FilterChain;
import collab.fm.server.filter.HibernateSessionFilter;
import collab.fm.server.filter.ProtocolFilter;
import collab.fm.server.util.ProtocolUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ProtocolInterpretException;

public class Controller {
	
	static Logger logger = Logger.getLogger(Controller.class);
	
	private static final String[] databaseAccessRequests = {
		Resources.REQ_COMMIT,
		Resources.REQ_LOGIN,
		Resources.REQ_REGISTER,
		Resources.REQ_UPDATE,
		Resources.REQ_CREATE_MODEL,
		Resources.REQ_LIST_MODEL,
		Resources.REQ_LISTUSER
	};
	
	private static Controller controller = new Controller();
	
	private static List<Action> actions = new ArrayList<Action>();
	
	private Filter accessValidator = new AccessValidator();
	private Filter actionDispatcher = new ActionDispatcher();
	private Filter protocolFilter = new ProtocolFilter();
	
	public static Controller instance() {
		return controller;
	}
	
	public static void init() {
		actions.add(new CommitAction());
		actions.add(new LoginAction());
		actions.add(new RegisterAction());
		actions.add(new UpdateAction());
		actions.add(new CreateModelAction());
		actions.add(new ListModelAction());
		actions.add(new ListUserAction());
		actions.add(new EditFeatureAction());
		logger.info("Controller initialized.");
	}
	
	private Controller() {

	}
	
	public void registerAction(String[] names, Action action) {
		((ActionDispatcher)this.actionDispatcher).registerAction(names, action);
	}
	
	public void disconnectUser(String addr) {
		accessValidator.onClientDisconnected(addr);
		actionDispatcher.onClientDisconnected(addr);
		protocolFilter.onClientDisconnected(addr);
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
			req = ProtocolUtil.jsonToRequest(message);
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
		chain.addFilter(protocolFilter);
		chain.addFilter(accessValidator);
		if (needDatabaseAccess(requestName)) {
			// Session per request
			chain.addFilter(new HibernateSessionFilter());
		}
		chain.addFilter(actionDispatcher);
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
	
	private void convertResponsesToJson(ResponseGroup rg) throws ProtocolInterpretException {
		rg.setJsonBack(ProtocolUtil.ResponseToJson(rg.getBack()));
		rg.setJsonBroadcast(ProtocolUtil.ResponseToJson(rg.getBroadcast()));
		rg.setJsonPeer(ProtocolUtil.ResponseToJson(rg.getPeer()));
	}
	
}
