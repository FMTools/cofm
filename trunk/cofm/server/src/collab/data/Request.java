package collab.data;

import java.net.InetSocketAddress;

import collab.filter.Filterable;

public class Request extends Filterable {
	public static final String TERMINATOR = "\n";
	public static final String TYPE_UPDATE = "update";
	public static final String TYPE_COMMIT = "commit";
	
	private InetSocketAddress source;
	private Object body; 
	
	public Request(InetSocketAddress address, Object content) {
		source = address;
		body = content;
	}
	
	public Object body() {
		return body;
	}
	
	public void body(Object newContent) {
		body = newContent;
	}

}
