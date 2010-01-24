package collab.fm.client.util {
	import mx.resources.ResourceBundle;

	public class Cst {
		public static const FIELD_RSP_NAME: String = "name";
		public static const FIELD_RSP_SOURCE_USER_ID: String = "requesterId";
		public static const FIELD_RSP_SOURCE_NAME: String = "requestName";
		public static const FIELD_RSP_SOURCE_ID: String = "requestId";

		public static const REQ_DELIMITER: String = "<[!END]]>";
		public static const REQ_COMMIT: String = "commit";
		public static const REQ_UPDATE: String = "update";
		public static const REQ_LOGIN: String = "login";
		public static const REQ_LOGOUT: String = "logout";
		public static const REQ_LIST_USER: String = "listuser";
		public static const REQ_LIST_MODEL: String = "listmodel";
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

		public static const DATA_MY_INFO: String = "myInfoUpdated";
		public static const DATA_USER_NAMES: String = "userNamesUpdated";
		public static const DATA_OTHERS_MODEL: String = "othersModelUpdated";
		public static const DATA_CREATE_MODEL: String = "myModelCreated";

	}
}