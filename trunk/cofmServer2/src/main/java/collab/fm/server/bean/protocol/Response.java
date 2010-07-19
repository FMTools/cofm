package collab.fm.server.bean.protocol;

import collab.fm.server.util.JsonUtil;
import collab.fm.server.util.exception.JsonConvertException;

public class Response implements Cloneable {
	public static final String TERMINATOR = "\0";
	
	private String name;
	
	private Long requestId;
	private String requestName;
	private Long requesterId;
	
	private String message;
	
	public Response() {
		
	}
	
	public Response(Request r) {
		if (r == null) {
			return;
		}
		this.setRequesterId(r.getRequesterId());
		this.setRequestId(r.getId());
		this.setRequestName(r.getName());
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean valid() {
		return name != null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Long getRequestId() {
		return requestId;
	}


	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}


	public String getRequestName() {
		return requestName;
	}


	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}


	public Long getRequesterId() {
		return requesterId;
	}


	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
	}
	
}
