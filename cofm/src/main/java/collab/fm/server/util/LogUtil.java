package collab.fm.server.util;


public class LogUtil {

	public static int MAX_TEXT_LEN = 80;
	
	public static final String OP_CREATE = "create";
	public static final String OP_VOTE = "vote";
	public static final String OP_REMOVE = "remove";
	public static final String OP_REGISTER = "register";
	public static final String OP_LOGIN = "login";
	public static final String OP_LOGOUT = "logout";
	
	public static final String OBJ_ATTR = "A";
	public static final String OBJ_MODEL = "M";
	public static final String OBJ_FEATURE = "F";
	public static final String OBJ_RELATION = "R";
	public static final String OBJ_VALUE = "V";
	
	public static final String OBJ_VALUE_OF_MODEL_ATTR = "VM";
	
	
	public static String logOp(Long userId, String op, String obj) {
		return "[fm] u#" + userId + " " + op + " " + obj;
	}
	
	public static String modelOrAttrToStr(String objType, Long modelId, Object details) {
		return objType + " m#" + modelId + " " + details.toString();
	}
	
	public static String featureOrAttrToStr(String objType, Long modelId, Long featureId, Object details) {
		return objType + " m#" + modelId + " f#" + featureId + " " + details.toString();
	}
	
	public static String relationToStr(String objType, Long modelId, Long relationId, Object details) {
		// objType == OBJ_RELATION now. (Keep the parameter for ease of extension.)
		return objType + " m#" + modelId + " r#" + relationId + " " + details.toString();
	}
	
	public static String boolToVote(boolean yes) {
		if (yes) return OP_VOTE + " YES";
		return OP_VOTE + " NO";
	}
	
	public static String truncText(String text) {
		return truncText(text, MAX_TEXT_LEN);
	}
	
	public static String truncText(String text, int maxLen) {
		if (text.length() > maxLen) return text.substring(0, maxLen-3) + "...";
		return text;
	}
	
}
