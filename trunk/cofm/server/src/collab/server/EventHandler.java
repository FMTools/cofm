package collab.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;
import org.apache.mina.util.SessionUtil;

import collab.data.*;

/**
 *  ThreadSafe: YES
 */
public class EventHandler extends IoHandlerAdapter {
	
	private ConcurrentHashMap<String, IoSession> sessionMap = 
		new ConcurrentHashMap<String, IoSession>();
	
	//~sketch
	private Controller controller;
	
	public EventHandler(Controller c) {
		controller = c;
	}
	///~

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		Request req = new Request();
		req.setAddress(session.getRemoteAddress().toString());
		req.setData(message);
		List<Response> rsp = controller.handleRequest(req);
		if (rsp != null) {
			for (Response r: rsp) {
				distributeResponse(session, r);
			}
		}
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
    
    private void distributeResponse(IoSession session, Response rsp) {
    	// Write back to requester
    	if (Response.TYPE_BACK.equals(rsp.getType())) {
    		writeResponse(session.getRemoteAddress().toString(), rsp);
    	}
    	
    	// Do multicast or broadcast
    	if (Response.TYPE_PEER.equals(rsp.getType())) {
    		List<String> dest = rsp.getTargets();
    		for (String addr: dest) {
    			writeResponse(addr, rsp);
    		}
    		
    	} else if (Response.TYPE_BROADCAST.equals(rsp.getType())) {
    		Set<String> addrs = sessionMap.keySet();
    		for (String addr: addrs) {
    			writeResponse(addr, rsp);
    		}
    	}
    }
    
    private void writeResponse(String address, Response rsp) {
    	IoSession session = sessionMap.get(address);
    	if (session != null) {
    		session.write((String)rsp.getBody());
    	}
    }
}
