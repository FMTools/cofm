package quiz.server.request;

public class Response {

	public static final String[] messages = {
		"OK", "Bad Request", "Handle Error"
	};
	public static final int STATUS_OK = 0;
	public static final int STATUS_BAD = 1;
	public static final int STATUS_ERROR = 2;
	
	
	private int status;
	private String message;
	
	public Response(int status) {
		this.status = status;
		this.message = messages[status];
	}
	
	public Response(int status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}
	
	
}
