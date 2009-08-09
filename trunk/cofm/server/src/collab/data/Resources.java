package collab.data;

import java.lang.reflect.*;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Resources {
	//Protocol
	public static final String REQ_FIELD_NAME = "req.field.name";
	public static final String REQ_FIELD_ID = "req.field.id";
	public static final String REQ_FIELD_USER = "req.field.user";
	public static final String REQ_FIELD_DATA = "req.field.data";
	
	public static final String REQ_ERROR_AUTHORITY = "req.error.authority";
	public static final String REQ_ERROR_FORMAT = "req.error.format";
	
	public static final String REQ_UPDATE = "req.update";
	public static final String REQ_COMMIT = "req.commit";
	public static final String REQ_LOGIN = "req.login";
	public static final String REQ_LOGOUT = "req.logout";
	public static final String REQ_CONNECT = "req.connect";
	public static final String REQ_LISTUSER = "req.listuser";

	public static final String RSP_ERROR_FORMAT = "rsp.error.format";
	
	public static final String RSP_SUCCESS = "rsp.success";
	public static final String RSP_FORWARD = "rsp.forward";
	public static final String RSP_DENIED = "rsp.denied";
	public static final String RSP_ERROR = "rsp.error";
	
	public static final String OP_FIELD_OP = "op.field.op";
	public static final String OP_FIELD_LEFT = "op.field.left";
	public static final String OP_FIELD_RIGHT = "op.field.right";
	public static final String OP_FIELD_VOTE = "op.field.vote";
	
	public static final String OP_ADDCHILD = "op.addChild";
	public static final String OP_ADDREQUIRE = "op.addRequire";
	public static final String OP_ADDEXCLUDE = "op.addExclude";
	public static final String OP_ADDNAME = "op.addName";
	public static final String OP_ADDDES = "op.addDes";
	public static final String OP_SETOPT = "op.setOpt";
	public static final String OP_SETEXT = "op.setExt";
	
	public static final String MSG_ERROR_EXCEPTION = "msg.error.exception";
	public static final String MSG_ERROR_CONSTRAINT = "msg.error.constraint";
	public static final String MSG_ERROR_REQUEST = "msg.error.request";
	
	public static final String MSG_ERROR_FIELD_MISSING = "msg.error.field.missing";
	public static final String MSG_ERROR_FIELD_VALUE = "msg.error.field.value";
		
	public static final String MSG_ERROR_USER_DENIED = "msg.error.user.denied";
	public static final String MSG_ERROR_USER_NOTFOUND = "msg.error.user.notfound";
	
	public static final String MSG_ERROR_FEATURE_NOTFOUND = "msg.error.feature.notfound";
	public static final String MSG_ERROR_FEATURE_APPLYOP = "msg.error.feature.applyop";
	
	public static final String MSG_ERROR_PERSISTENT_WRITE = "msg.error.persistent.write";
	public static final String MSG_ERROR_PERSISTENT_GET = "msg.error.persistent.get";
	
	private static ResourceBundle protocolRes = ResourceBundle.getBundle("protocol");
	private static ResourceBundle msgRes = ResourceBundle.getBundle("locale.message");
	
	private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
	
	static {
		try {
			Class thisClass = Class.forName("collab.data.Resources");
			Field[] fields = thisClass.getFields();
			for (Field f: fields) {
				String key = (String)f.get(null);
				try {
					map.put(key, protocolRes.getString(key));
				} catch (Exception e) {
					map.put(key, msgRes.getString(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key) {
		return map.get(key);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		Class cRes = Class.forName("collab.data.Resources");
		Field[] fields = cRes.getFields();
		int i = 1;
		for (Field f: fields) {
			String val = (String)f.get(null);
			System.out.println((i++) + ": " + val + " = " + Resources.get(val));
		}
	}
}
