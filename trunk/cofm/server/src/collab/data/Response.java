package collab.data;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import collab.filter.Filterable;

public class Response extends Filterable {
	public static final String TERMINATOR = "\0";
	
	public static final String TYPE_PEER = "peer";
	public static final String TYPE_BROADCAST = "broadcast";

	private String type;
	private boolean sendBack; // write response back to requester ?
	private ArrayList<InetSocketAddress> targets = null; // for other targets
	
	private Object body; // "name" is a field of body
		
	public Response(String type, Object content, boolean sendBack) {
		this.type = type;
		this.sendBack = sendBack;
		body = content;
	}
	
	public ArrayList<InetSocketAddress> targets() {
		return targets;
	}
	
	public void addTarget(InetSocketAddress address) {
		if (targets == null) {
			targets = new ArrayList<InetSocketAddress>();
		}
		targets.add(address);
	}
	
	public String type() {
		return type;
	}
	
	public void type(String t) {
		type = t;
	}
	
	public Object body() {
		return body;
	}
	
	public void body(Object newBody) {
		body = newBody;
	}
	
	public boolean sendBack() {
		return sendBack;
	}
	
	public void sendBack(boolean back) {
		sendBack = back;
	}
}
