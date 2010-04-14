package collab.fm.server.controller;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import collab.fm.server.bean.protocol.ResponseGroup;

/**
 *  ThreadSafe: YES
 */
public class EventHandler extends IoHandlerAdapter {
	
	static Logger logger = Logger.getLogger(EventHandler.class);
	
	private static final String CROSS_DOMAIN_POLICY = 
		"<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" /></cross-domain-policy>";
	
	private ConcurrentHashMap<String, IoSession> sessionMap = 
		new ConcurrentHashMap<String, IoSession>();
	

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		logger.info("--- Request received from '" + session.getRemoteAddress().toString() + "'");
		if (((String)message).startsWith("<policy-file-request")) {
			session.write(CROSS_DOMAIN_POLICY);
			return;
		}
		ResponseGroup group = 
			Controller.instance().execute((String)message, session.getRemoteAddress().toString());
		distributeResponse(session, group);
		logger.info("--- Response sent.");
	}

	public void messageSent(IoSession session, Object message) throws Exception {
	}
	
    public void sessionOpened(IoSession session) throws Exception {
    	logger.info("--- Connection established.");
    	sessionMap.put(session.getRemoteAddress().toString(), session);
    }

    public void sessionClosed(IoSession session) throws Exception {
    	logger.info("--- Connection closed.");
    	Controller.instance().disconnectUser(session.getRemoteAddress().toString());
    	sessionMap.remove(session.getRemoteAddress().toString());
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }
    
    private void distributeResponse(IoSession session, ResponseGroup group) {
    	// Write back to requester
    	
    	if (group.getJsonBack() != null) {
    		session.write(group.getJsonBack());
    	}
    	
    	// Do multicast or broadcast
    	if (group.getJsonPeer() != null) {
    		List<String> dest = group.getTargets();
    		for (String addr: dest) {
    			forwardResponse(session, addr, group.getJsonPeer());
    		}
    		
    	} 
    	
    	if (group.getJsonBroadcast() != null) {
    		Set<String> addrs = sessionMap.keySet();
    		for (String addr: addrs) {
    			forwardResponse(session, addr, group.getJsonBroadcast());
    		}
    	}
    }
    
    private void forwardResponse(IoSession session, String address, String message) {
    	if (address.equals(session.getRemoteAddress().toString())) {
    		return;
    	}
    	IoSession target = sessionMap.get(address);
    	if (target != null) {
    		target.write(message);
    	}
    }
}
