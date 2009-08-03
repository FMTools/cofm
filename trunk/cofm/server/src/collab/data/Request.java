package collab.data;

import java.net.InetSocketAddress;

import collab.filter.Filterable;

public class Request extends Filterable {
	public static final String TERMINATOR = "\n";
	
	private InetSocketAddress address;
	private Object body; 
	
	public Request(InetSocketAddress address, Object body) {
		this.address = address;
		this.body = body;
	}
	
	public Object body() {
		return body;
	}
	
	public void body(Object newContent) {
		body = newContent;
	}
	
	public InetSocketAddress address() {
		return address;
	}

}
