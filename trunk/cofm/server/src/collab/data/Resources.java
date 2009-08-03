package collab.data;

import java.util.ResourceBundle;

public class Resources {
	//Protocol
	public static final String REQ_FIELD_NAME = "req.field.name";
	public static final String REQ_FIELD_ID = "req.field.id";
	public static final String REQ_FIELD_USER = "req.field.user";
	public static final String REQ_ERROR_AUTHORITY = "req.error.authority";
	public static final String REQ_ERROR_FORMAT = "req.error.format";
	public static final String REQ_UPDATE = "req.update";
	public static final String REQ_COMMIT = "req.commit";
	public static final String REQ_LOGIN = "req.login";
	public static final String REQ_LOGOUT = "req.logout";
	public static final String REQ_CONNECT = "req.connect";
	
	public static final String RSP_FIELD_NAME = "rsp.field.name";
	public static final String RSP_FIELD_SOURCE_ID = "rsp.field.source.id";
	public static final String RSP_FIELD_SOURCE_NAME = "rsp.field.source.name";
	public static final String RSP_FIELD_SOURCE_ADDR = "rsp.field.source.addr";
	public static final String RSP_FIELD_SOURCE_USER = "rsp.field.source.user";
	public static final String RSP_ERROR_FORMAT = "rsp.error.format";
	public static final String RSP_SUCCESS = "rsp.success";
	public static final String RSP_FORWARD = "rsp.forward";
	public static final String RSP_DENIED = "rsp.denied";
	public static final String RSP_FAILURE = "rsp.failure";
	
	public static final String MSG_ERROR_DENIED = "msg.error.denied";	
	
	private static ResourceBundle protocolRes = ResourceBundle.getBundle("protocol");
	
	public static String get(String key) {
		//TODO: handle more locale resources
		return protocolRes.getString(key);
	}

}
