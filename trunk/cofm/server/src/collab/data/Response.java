package collab.data;

import java.net.InetSocketAddress;
import java.util.ArrayList;


public class Response extends Filterable {
	public static final String TERMINATOR = "\0";
	
	public static final String BODY_CLASS_NAME = "RspBody";
	
	public static final String TYPE_PEER = "peer";
	public static final String TYPE_BROADCAST = "broadcast";

	private String type;
	private ArrayList<InetSocketAddress> targets = null; // for other targets
	
	private Object body; // "name" is a field of body
		
	public Response(String type, Object content) {
		this.type = type;
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
}
