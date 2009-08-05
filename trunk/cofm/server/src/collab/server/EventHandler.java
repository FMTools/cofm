package collab.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
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
	
	private ConcurrentHashMap<InetSocketAddress, IoSession> sessionMap = 
		new ConcurrentHashMap<InetSocketAddress, IoSession>();
	
	//~sketch
	private Controller controller;
	
	public EventHandler(Controller c) {
		controller = c;
	}
	///~

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		Request req = new Request(
				(InetSocketAddress)session.getRemoteAddress(), 
				message);
		Response rsp = (Response)controller.handleRequest(req);
		distributeResponse(session, rsp);
	}

	public void messageSent(IoSession session, Object message) throws Exception {
	}
	
    public void sessionOpened(IoSession session) throws Exception {
    	sessionMap.put((InetSocketAddress)session.getRemoteAddress(), session);
    }

    public void sessionClosed(IoSession session) throws Exception {
    	sessionMap.remove((InetSocketAddress)session.getRemoteAddress());
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }
    
    private void distributeResponse(IoSession session, Response rsp) {
    	// Write back to requester
    	writeResponse((InetSocketAddress)session.getRemoteAddress(), rsp);
    	
    	// Do multicast or broadcast
    	if (rsp.type().equals(Response.TYPE_PEER)) {
    		ArrayList<InetSocketAddress> dest = rsp.targets();
    		Iterator<InetSocketAddress> it = dest.iterator();
    		writeResponseForAll(session, it, rsp);
    		
    	} else if (rsp.type().equals(Response.TYPE_BROADCAST)) {
    		Set<InetSocketAddress> addrs = sessionMap.keySet();
    		Iterator<InetSocketAddress> it = addrs.iterator();
    		writeResponseForAll(session, it, rsp);
    	}
    }
    
    private void writeResponseForAll(IoSession session, Iterator<InetSocketAddress> it, Response rsp) {
    	while (it.hasNext()) {
			InetSocketAddress addr = it.next();
			if (addr.equals((InetSocketAddress)session.getRemoteAddress())) {
				continue;
			}
			writeResponse(addr, rsp);
		}
    }
    
    private void writeResponse(InetSocketAddress address, Response rsp) {
    	IoSession session = sessionMap.get(address);
    	if (session != null) {
    		session.write((String)rsp.body());
    	}
    }
}
