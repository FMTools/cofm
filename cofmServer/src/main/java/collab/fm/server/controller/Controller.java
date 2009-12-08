package collab.fm.server.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.filter.*;
import collab.fm.server.action.*;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.ProtocolUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.FilterException;
import collab.fm.server.util.exception.JsonConvertException;
import collab.fm.server.util.exception.ProtocolInterpretException;

public class Controller {
	
	static Logger logger = Logger.getLogger(Controller.class);
	
	private static final String[] databaseAccessRequests = {
		Resources.REQ_COMMIT,
		Resources.REQ_LOGIN,
		Resources.REQ_REGISTER,
		Resources.REQ_UPDATE
	};
	
	private static Controller controller = new Controller();
	
	private Filter accessValidator;
	private Filter actionDispatcher;
	private Filter protocolFilter;
	
	public static Controller instance() {
		return controller;
	}
	
	private Controller() {
		accessValidator = new AccessValidator();
		actionDispatcher = new ActionDispatcher();
		protocolFilter = new ProtocolFilter();
	}
	
	public void registerAction(String[] names, Action action) {
		((ActionDispatcher)this.actionDispatcher).registerAction(names, action);
	}
	
	public ResponseGroup execute(String message, String sourceAddress) {
		Request req = null;
		ResponseGroup rg = new ResponseGroup();
		try {
			// 1. Build request from message (a JSON string actually)
			req = ProtocolUtil.jsonToRequest(message);
			req.setAddress(sourceAddress);
			
			// 2. Build a chain to process the request
			FilterChain chain = buildChain(req.getName());
			
			chain.doNextFilter(req, rg);
			convertResponsesToJson(rg);
			
		} catch (Exception e) {
			logger.warn("Couldn't process request: " + req.getLastError(), e);
			reportErrorToRequester(req, rg);
			try {
				convertResponsesToJson(rg);
			} catch (Exception ex) {
				logger.fatal("Internal error in Controller.execute: should never happen.", ex);
				throw new RuntimeException("Fatal: should never reach here.", ex);
			}
		}
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
	
	private void reportErrorToRequester(Request req, ResponseGroup rg) {
		// Disallow multicast and broadcast
		rg.setBroadcast(null);
		rg.setPeer(null);
		rg.setTargets(null);
		
		Response rsp = new Response();
		rsp.setMessage(req.getLastError());
		rsp.setName(Resources.RSP_ERROR);
		if (req != null) {
			rsp.setRequesterId(req.getRequesterId());
			rsp.setRequestId(req.getId());
			rsp.setRequestName(req.getName());
		}
		rg.setBack(rsp);
	}
	
	private void convertResponsesToJson(ResponseGroup rg) throws ProtocolInterpretException {
		rg.setJsonBack(ProtocolUtil.ResponseToJson(rg.getBack()));
		rg.setJsonBroadcast(ProtocolUtil.ResponseToJson(rg.getBroadcast()));
		rg.setJsonPeer(ProtocolUtil.ResponseToJson(rg.getPeer()));
	}
	
}
