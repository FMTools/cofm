package cofm.util
{
	/**
	 * Constants used in the code.
	 */
	public class Cst {
		public static const FIELD_RSP_NAME: String = "name";
		public static const FIELD_RSP_SOURCE_USER_ID: String = "requesterId";
		public static const FIELD_RSP_SOURCE_NAME: String = "requestName";
		public static const FIELD_RSP_SOURCE_ID: String = "requestId";
		public static const FIELD_RSP_MESSAGE: String = "message";
		public static const FIELD_RSP_VOTE: String = "yes";
		public static const FIELD_RSP_EXIST: String = "exist";
		public static const FIELD_RSP_INFER_VOTES: String = "inferVotes";
		
		
		public static const REQ_VA_FEATURE: String  = "vaFeature";
		public static const REQ_VA_BIN_REL: String  = "vaRelationBin";
		public static const REQ_VA_ATTR: String  = "vaAttrStr";
		public static const REQ_VA_ATTR_ENUM: String  = "vaAttrEnum";
		public static const REQ_VA_ATTR_NUMBER: String  = "vaAttrNumber";
		public static const REQ_VA_VALUE: String = "vaValue";
		
		public static const REQ_COMMENT: String = "comment";
		public static const REQ_EXIT_MODEL: String = "exitModel";
		public static const REQ_FOCUS: String = "focus";
		public static const REQ_UPDATE: String = "update";
		public static const REQ_LOGIN: String = "login";
		public static const REQ_LOGOUT: String = "logout";
		public static const REQ_LIST_USER: String = "listUser";
		public static const REQ_LIST_MODEL: String = "listModel";
		public static const REQ_REGISTER: String = "register";
		public static const REQ_CREATE_MODEL: String = "createModel";
		
		public static const RSP_SUCCESS: String = "success";
		public static const RSP_ERROR: String = "error";
		public static const RSP_STALE: String = "stale";
		public static const RSP_FORWARD: String = "forward";
		public static const RSP_SERVER_ERROR: String = "serverError";
		
		public static const OP_CREATE_FEATURE: String = "createFeature";
		public static const OP_CREATE_RELATIONSHIP: String = "createRelationship";
		public static const OP_ADD_NAME: String = "addName";
		public static const OP_ADD_DES: String = "addDes";
		public static const OP_SET_OPT: String = "setOpt";
		
		public static const BIN_REL_REFINES: String = "refines";
		public static const BIN_REL_REQUIRES: String = "requires";
		public static const BIN_REL_EXCLUDES: String = "excludes";
		
		public static const ATTR_TYPE_STRING: String = "string";
		public static const ATTR_TYPE_TEXT: String = "text";
		public static const ATTR_TYPE_ENUM: String = "enum";
		public static const ATTR_TYPE_NUMBER: String = "number";
		public static const ATTR_TYPE_LIST: String = "list";
		
		public static const ATTR_FEATURE_NAME: String = "Name";
		public static const ATTR_FEATURE_DES: String = "Description";
		public static const ATTR_FEATURE_OPT: String = "Optionality";
		public static const ATTR_MODEL_NAME: String = "Model Name";
		public static const ATTR_MODEL_DES: String = "Model Description";
		
		public static const VAL_OPT_MAN: String = "Mandatory";
		public static const VAL_OPT_OPT: String = "Optional";
	}
}