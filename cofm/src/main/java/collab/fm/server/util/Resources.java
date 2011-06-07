package collab.fm.server.util;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

public class Resources {
	
	private static ResourceBundle protocolRes = ResourceBundle.getBundle("protocol");
	private static ResourceBundle msgRes = ResourceBundle.getBundle("locale.message");
	
	// Default entity type
	public static final String ENTITY_FEATURE = get("entity.feature");
	// Attribute Keys
	public static final String ATTR_ENTITY_NAME = get("attr.entity.name");
	public static final String ATTR_ENTITY_DES = get("attr.entity.des");
	public static final String ATTR_FEATURE_OPT = get("attr.feature.opt");
	public static final String ATTR_MODEL_NAME = get("attr.model.name");
	public static final String ATTR_MODEL_DES = get("attr.model.des");
	
	public static final String VAL_OPT_MANDATORY = get("val.opt.man");
	public static final String VAL_OPT_OPTIONAL = get("val.opt.opt");
	
	// Protocol
	public static final String REQ_ERROR_AUTHORITY = get("req.error.authority");
	public static final String REQ_ERROR_FORMAT = get("req.error.format");
	
	public static final String REQ_COMMENT = get("req.comment");
	public static final String REQ_EXIT_MODEL = get("req.exitModel");
	public static final String REQ_FOCUS_ON_FEATURE = get("req.focus");
	public static final String REQ_UPDATE = get("req.update");
	public static final String REQ_LOGIN = get("req.login");
	public static final String REQ_LOGOUT = get("req.logout");
	public static final String REQ_CONNECT = get("req.connect");
	public static final String REQ_LIST_USER = get("req.listUser");
	public static final String REQ_LIST_MODEL = get("req.listModel");
	public static final String REQ_REGISTER = get("req.register");
	public static final String REQ_CREATE_MODEL = get("req.createModel");
	
	public static final String REQ_VA_ENTITY = get("req.va.entity");
	public static final String REQ_VA_RELATION_BIN = get("req.va.relation.bin");
	public static final String REQ_VA_VALUE = get("req.va.value");
	
	public static final String REQ_EA_ATTR_STR = get("req.ea.attr.str");
	public static final String REQ_EA_ATTR_ENUM = get("req.ea.attr.enum");
	public static final String REQ_EA_ATTR_NUMBER = get("req.ea.attr.number");
	public static final String REQ_EA_ENTITY_TYPE = get("req.ea.entitytype");
	public static final String REQ_EA_BIN_RELAION_TYPE = get("req.ea.reltype.bin");
	
	public static final String RSP_ERROR_FORMAT = get("rsp.error.format");
	
	public static final String RSP_SUCCESS = get("rsp.success");
	public static final String RSP_FORWARD = get("rsp.forward");
	public static final String RSP_ERROR = get("rsp.error");
	public static final String RSP_STALE = get("rsp.stale");
	public static final String RSP_SERVER_ERROR = get("rsp.serverError");

	public static final String BIN_REL_REFINES = get("binrel.refines");
	public static final String BIN_REL_REQUIRES = get("binrel.requires");
	public static final String BIN_REL_EXCLUDES = get("binrel.excludes");

	//Message
	public static final String MSG_ERROR_EXCEPTION = get("msg.error.exception");
	public static final String MSG_ERROR_CONSTRAINT = get("msg.error.constraint");
	public static final String MSG_ERROR_REQUEST = get("msg.error.request");
	
	public static final String MSG_ERROR_FIELD_MISSING = get("msg.error.field.missing");
	public static final String MSG_ERROR_FIELD_VALUE = get("msg.error.field.value");
		
	public static final String MSG_ERROR_USER_DENIED = get("msg.error.user.denied");
	public static final String MSG_ERROR_USER_LOGIN_FAILED = get("msg.error.user.loginfailed");
	public static final String MSG_ERROR_USER_NO_VERIFICATION = get("msg.error.user.novalidation");
	public static final String MSG_ERROR_USER_EXISTED = get("msg.error.user.existed");
	public static final String MSG_ERROR_USER_LOGIN_REPEAT = get("msg.error.user.login.repeat");
	
	public static final String MSG_ERROR_FEATURE_NOTFOUND = get("msg.error.feature.notfound");
	public static final String MSG_ERROR_FEATURE_APPLYOP = get("msg.error.feature.applyop");
	
	public static final String MSG_ERROR_PERSISTENT_WRITE = get("msg.error.persistent.write");
	public static final String MSG_ERROR_PERSISTENT_GET = get("msg.error.persistent.get");
	
	public static final String MSG_ERROR_STALE = get("msg.error.stale");
	
	public static final String MSG_LOGIN = get("msg.login");
	public static final String MSG_REGISTER = get("msg.register");
	public static final String MSG_REGISTER_MAIL_TITLE = get("msg.register.mail.title");
	public static final String MSG_REGISTER_MAIL_TEXT = get("msg.register.mail.content");
	
	private static String get(String key) {
		try {
			return protocolRes.getString(key);
		} catch (Exception e) {
			return msgRes.getString(key);
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		Class cRes = Class.forName("collab.fm.server.util.Resources");
		Field[] fields = cRes.getFields();
		int i = 1;
		for (Field f: fields) {
			String val = null;
			try {
				val = (String)f.get(null);
			} catch (Exception e) {
				val = String.valueOf(f.get(null));
			}
			System.out.println((i++) + ": " + val);
		}
	}
}
