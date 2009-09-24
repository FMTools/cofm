package collab.fm.server.util;

import java.lang.reflect.*;
import java.util.ResourceBundle;

public class Resources {
	
	private static ResourceBundle protocolRes = ResourceBundle.getBundle("protocol");
	private static ResourceBundle msgRes = ResourceBundle.getBundle("locale.message");
	
	//Protocol
	public static final String REQ_FIELD_NAME = get("req.field.name");
	public static final String REQ_FIELD_ID = get("req.field.id");
	public static final String REQ_FIELD_USER = get("req.field.user");
	public static final String REQ_FIELD_DATA = get("req.field.data");
	
	public static final String REQ_ERROR_AUTHORITY = get("req.error.authority");
	public static final String REQ_ERROR_FORMAT = get("req.error.format");
	
	public static final String REQ_UPDATE = get("req.update");
	public static final String REQ_COMMIT = get("req.commit");
	public static final String REQ_LOGIN = get("req.login");
	public static final String REQ_LOGOUT = get("req.logout");
	public static final String REQ_CONNECT = get("req.connect");
	public static final String REQ_LISTUSER = get("req.listuser");
	public static final String REQ_REGISTER = get("req.register");

	public static final String RSP_ERROR_FORMAT = get("rsp.error.format");
	
	public static final String RSP_SUCCESS = get("rsp.success");
	public static final String RSP_FORWARD = get("rsp.forward");
	public static final String RSP_DENIED = get("rsp.denied");
	public static final String RSP_ERROR = get("rsp.error");
	
	public static final String OP_FIELD_OP = get("op.field.op");
	public static final String OP_FIELD_LEFT = get("op.field.left");
	public static final String OP_FIELD_RIGHT = get("op.field.right");
	public static final String OP_FIELD_VOTE = get("op.field.vote");
	
	public static final String OP_ADDCHILD = get("op.addChild");
	public static final String OP_ADDREQUIRE = get("op.addRequire");
	public static final String OP_ADDEXCLUDE = get("op.addExclude");
	public static final String OP_ADDNAME = get("op.addName");
	public static final String OP_ADDDES = get("op.addDes");
	public static final String OP_SETOPT = get("op.setOpt");
	public static final String OP_SETEXT = get("op.setExt");
	
	//Message
	public static final String MSG_ERROR_EXCEPTION = get("msg.error.exception");
	public static final String MSG_ERROR_CONSTRAINT = get("msg.error.constraint");
	public static final String MSG_ERROR_REQUEST = get("msg.error.request");
	
	public static final String MSG_ERROR_FIELD_MISSING = get("msg.error.field.missing");
	public static final String MSG_ERROR_FIELD_VALUE = get("msg.error.field.value");
		
	public static final String MSG_ERROR_USER_DENIED = get("msg.error.user.denied");
	public static final String MSG_ERROR_USER_NOTFOUND = get("msg.error.user.notfound");
	
	public static final String MSG_ERROR_FEATURE_NOTFOUND = get("msg.error.feature.notfound");
	public static final String MSG_ERROR_FEATURE_APPLYOP = get("msg.error.feature.applyop");
	
	public static final String MSG_ERROR_PERSISTENT_WRITE = get("msg.error.persistent.write");
	public static final String MSG_ERROR_PERSISTENT_GET = get("msg.error.persistent.get");
	
	private static String get(String key) {
		try {
			return protocolRes.getString(key);
		} catch (Exception e) {
			return msgRes.getString(key);
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		Class cRes = Class.forName("collab.fm.server.bean.Resources");
		Field[] fields = cRes.getFields();
		int i = 1;
		for (Field f: fields) {
			String val = (String)f.get(null);
			System.out.println((i++) + ": " + val);
		}
	}
}
