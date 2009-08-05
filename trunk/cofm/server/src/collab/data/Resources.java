package collab.data;

import java.lang.reflect.*;
import java.util.ResourceBundle;

public class Resources {
	//Protocol
	public static final String REQ_FIELD_NAME = "req.field.name";
	public static final String REQ_FIELD_ID = "req.field.id";
	public static final String REQ_FIELD_USER = "req.field.user";
	public static final String REQ_FIELD_DATA = "req.field.data";
	public static final String REQ_FIELD_DATA_OP = "req.field.data.op";
	public static final String REQ_FIELD_DATA_LEFT = "req.field.data.left";
	public static final String REQ_FIELD_DATA_RIGHT = "req.field.data.right";
	public static final String REQ_FIELD_DATA_VOTE = "req.field.data.vote";
	
	public static final String REQ_VOTE_YES = "req.vote.yes";
	public static final String REQ_VOTE_NO = "req.vote.no";
	
	public static final String REQ_OP_ADDCHILD = "req.op.addChild";
	public static final String REQ_OP_ADDREQUIRE = "req.op.addRequire";
	public static final String REQ_OP_ADDEXCLUDE = "req.op.addExclude";
	public static final String REQ_OP_ADDNAME = "req.op.addName";
	public static final String REQ_OP_ADDDES = "req.op.addDes";
	public static final String REQ_OP_SETOPT = "req.op.setOpt";
	
	public static final String REQ_ERROR_AUTHORITY = "req.error.authority";
	public static final String REQ_ERROR_FORMAT = "req.error.format";
	
	public static final String REQ_UPDATE = "req.update";
	public static final String REQ_COMMIT = "req.commit";
	public static final String REQ_LOGIN = "req.login";
	public static final String REQ_LOGOUT = "req.logout";
	public static final String REQ_CONNECT = "req.connect";
	
	public static final String RSP_FIELD_NAME = "rsp.field.name";
	public static final String RSP_FIELD_SOURCE = "rsp.field.source";
	public static final String RSP_FIELD_SOURCE_ID = "rsp.field.source.id";
	public static final String RSP_FIELD_SOURCE_NAME = "rsp.field.source.name";
	public static final String RSP_FIELD_SOURCE_ADDR = "rsp.field.source.addr";
	public static final String RSP_FIELD_SOURCE_USER = "rsp.field.source.user";
	public static final String RSP_FIELD_DATA = "rsp.field.data";
	public static final String RSP_FIELD_DATA_TYPE = "rsp.field.data.type";
	public static final String RSP_FIELD_DATA_VALUE = "rsp.field.data.value";
	
	public static final String RSP_TYPE_FEATURE = "rsp.type.feature";
	public static final String RSP_TYPE_OP = "rsp.type.op";
	
	public static final String RSP_ERROR_FORMAT = "rsp.error.format";
	
	public static final String RSP_SUCCESS = "rsp.success";
	public static final String RSP_FORWARD = "rsp.forward";
	public static final String RSP_DENIED = "rsp.denied";
	public static final String RSP_FAILURE = "rsp.failure";
	
	public static final String MSG_ERROR_EXCEPTION = "msg.error.exception";
	public static final String MSG_ERROR_DENIED = "msg.error.denied";	
	public static final String MSG_ERROR_MISSING = "msg.error.missing";
	public static final String MSG_ERROR_VALUE = "msg.error.value";
	
	private static ResourceBundle protocolRes = ResourceBundle.getBundle("protocol");
	
	public static String get(String key) {
		return protocolRes.getString(key);
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
