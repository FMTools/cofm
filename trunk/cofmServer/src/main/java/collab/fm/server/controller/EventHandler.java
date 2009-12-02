package collab.fm.server.controller;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import collab.fm.server.bean.protocol.ResponseGroup;

/**
 *  ThreadSafe: YES
 */
public class EventHandler extends IoHandlerAdapter {
	
	private ConcurrentHashMap<String, IoSession> sessionMap = 
		new ConcurrentHashMap<String, IoSession>();
	

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		ResponseGroup group = 
			Controller.instance().execute((String)message, session.getRemoteAddress().toString());
		distributeResponse(session, group);
	}

	public void messageSent(IoSession session, Object message) throws Exception {
	}
	
    public void sessionOpened(IoSession session) throws Exception {
    	sessionMap.put(session.getRemoteAddress().toString(), session);
    }

    public void sessionClosed(IoSession session) throws Exception {
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
