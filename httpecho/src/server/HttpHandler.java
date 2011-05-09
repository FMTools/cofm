package server;


import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.asyncweb.common.DefaultHttpResponse;
import org.apache.asyncweb.common.HttpRequest;
import org.apache.asyncweb.common.HttpResponseStatus;
import org.apache.asyncweb.common.MutableHttpResponse;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class HttpHandler extends IoHandlerAdapter {

	private static class ClientInfo {
		public IoSession session;
		public boolean active = true;
		public List<String> unsendMsg = new ArrayList<String>();
		
		public ClientInfo(IoSession clientSession) {
			session = clientSession;
		}
		
		public void attachNewSession(IoSession newSession) {
			if (session != null && !session.isClosing()) {
				session.close(false);  // Close old session
			}
			session = newSession;
			active = true;
		}
		
		public void sendResponse(String msg, HttpRequest req) {
			if (!active) {
				unsendMsg.add(msg);
			} else {
				StringBuffer s = new StringBuffer();
				for (String oldMsg: unsendMsg) {
					s.append(oldMsg + "\n");
				}
				s.append(msg);
				session.write(asHttpMessage(s.toString(), req));
				active = false;   // The HTTP session can only be written once.
				unsendMsg.clear();
			}
		}
		
		public void disconnect() {
			session.close(true);
		}
		
		public boolean isDead() {
			// An dead client (e.g. its NBO-List reaches the limit, or its inactive time
			// reaches the limit) will be removed from the <IP, <Session, NBO-list>> map.
			return false;
		}
	}
	
	private static final String CROSS_DOMAIN_POLICY = 
		"<?xml version=\"1.0\"?>" +
		"<cross-domain-policy>" +
		"<allow-http-request-headers-from domain=\"*\" to-ports=\"*\" headers=\"*\" secure=\"false\" />" +
		"<allow-access-from domain=\"*\" to-ports=\"*\" secure=\"false\" />" +
		"</cross-domain-policy>";
	
	private ConcurrentHashMap<Integer, ClientInfo> clients = 
		new ConcurrentHashMap<Integer, ClientInfo>();
	
	private int nextClientId = 1;
	
	public void exceptionCaught(IoSession session, Throwable t)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		HttpRequest req = (HttpRequest) message;

		String mode = req.getParameter("mode");
		if (mode == null) {
			session.write(asHttpMessage("<html><body>This is default response page. Please visit via the flash client.</body></html>", req));
			return;
		}
		// TODO Filter / Check the request before passing it to Controller.
		
		// Handle the request (just broadcast it to all clients)
		System.out.println(req.getParameters().toString());
		
		String body = req.getContent().getString(Charset.forName("UTF-8").newDecoder());
		if (body != null && body.startsWith("<policy-file-request")) {
			session.write(asHttpMessage(CROSS_DOMAIN_POLICY, req));
			System.out.println("Cross domain policy sent.");
			return;
		}
		
		// There are 4 modes: 
		//   Handshake:  the server responds the client's ID and create its ClientInfo object.
		//   Read: the server updates the client's Session.
		//   Write: the server broadcast the data sent by this client.
		//   Disconnect: the server remove the client's ClientInfo object.
		
		if (mode.equals("handshake")) {
			ClientInfo info = new ClientInfo(session);
			Integer id = nextClientId++;
			clients.put(id, info);
			info.sendResponse("_Handshake_" + id, req);
			return;
		}
		Integer clientId = Integer.valueOf(req.getParameter("clientId"));
		ClientInfo info = clients.get(clientId);
		// TODO: if info == null, throws an exception
		if (info == null) {
			return;
		}
		info.attachNewSession(session);
		if (mode.equals("write")) {
			String msg = req.getParameter("message");
			for (ClientInfo client: clients.values()) {
				if (client.isDead()) {
					client.disconnect();
					clients.remove(clientId);
				} else {
					client.sendResponse(msg, req);
				}
			}
		} else if (mode.equals("disconnect")) {
			info.disconnect();
			clients.remove(clientId);
		}
	}
	
	private static MutableHttpResponse asHttpMessage(String body, HttpRequest req) {
		MutableHttpResponse resp = new DefaultHttpResponse();
	
		IoBuffer buf = IoBuffer.allocate(body.length());
		buf.setAutoExpand(true);
		try {
			buf.putString(body, Charset.forName("UTF-8").newEncoder());
			buf.flip();
			resp.setContent(buf);
			resp.setStatus(HttpResponseStatus.OK);
			resp.setContentType("text/html");
		} catch (CharacterCodingException e) {
			e.printStackTrace();
			resp.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
		if (req != null) {
			resp.normalize(req);
		}
		return resp;
	}
	
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub

	}

	public void sessionClosed(IoSession session) throws Exception {
		// Session has been closed by the client, we broadcast the information.
		// TODO If we use Polling strategy, do not broadcast it.
	}

	public void sessionCreated(IoSession session) throws Exception {

	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO Send heart-beat message to client to check if the client is still working.
		//session.write(asHttpMessage("Idle", null));
	}

	public void sessionOpened(IoSession session) throws Exception {
		
		System.out.println("--- Connection established with: " + session.getRemoteAddress().toString());
		// 30 seconds' idle time on both read and write will trigger sessionIdle() be called.
		//session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30); 
	}
}
