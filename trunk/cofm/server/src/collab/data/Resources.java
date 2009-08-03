package collab.data;

import java.util.ResourceBundle;
import java.util.Locale;;

public class Resources {
	//Protocol
	public static final String PRTCL_NAME_FIELD = "p_name_field";
	
	public static final String REQ_UPDATE = "pc_update";
	public static final String REQ_COMMIT = "pc_commit";
	public static final String REQ_LOGIN = "pc_login";
	public static final String REQ_LOGOUT = "pc_logout";
	public static final String REQ_UNAUTHORIZED = "pc_unauthorized";
	public static final String REQ_BAD = "pc_bad_format";
	
	public static final String RSP_SUCCESS = "ps_success";
	public static final String RSP_DENIED = "ps_denied";
	public static final String RSP_NEW_OP = "ps_new_op";
	public static final String RSP_MODEL_ERROR = "ps_model_error";
	
	private static ResourceBundle protocolRes = ResourceBundle.getBundle("protocol");
	
	public static String get(String key) {
		//TODO: handle more locale resources
		return protocolRes.getString(key);
	}

}
