package collab.data;

import java.net.InetSocketAddress;

import collab.filter.Filterable;

public class Request extends Filterable {
	public static final String TERMINATOR = "\n";
	
	private InetSocketAddress source;
	private String name;
	private Object body; 
	
	public Request(InetSocketAddress address, Object content) {
		source = address;
		name = null;
		body = content;
	}
	
	public Object body() {
		return body;
	}
	
	public void body(Object newContent) {
		body = newContent;
	}
	
	public String name() {
		return name;
	}
	
	public void name(String n) {
		name = n;
	}
	
	public InetSocketAddress source() {
		return source;
	}

}
