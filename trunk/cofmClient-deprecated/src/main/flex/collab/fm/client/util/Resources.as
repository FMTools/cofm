package collab.fm.client.util
{
	import mx.resources.ResourceBundle;
	
	public class Resources
	{
		private static final var protocol: ResourceBundle = new ResourceBundle(null, "protocol.properties");
		
		public static final var CMD_UI_CREATE_FEATURE: String = get("cmd.ui.createFeature");
		
		public static final var FIELD_CMD_MAIN_NAME: String = "name";
		public static final var FIELD_CMD_ID: String = "id";
		public static final var FIELD_CMD_USER_ID: String = "requesterId";
		public static final var FIELD_CMD_SUB_NAME: String = "requestName";
		public static final var FIELD_CMD_ORIGIN_ID: String = "requestId";
		
		public static final var REQ_COMMIT: String = get("req.commit");
		public static final var REQ_UPDATE: String = get("req.update");
		public static final var REQ_LOGIN: String = get("req.login");
		public static final var REQ_LOGOUT: String = get("req.logout");
		public static final var REQ_LIST_USER: String = get("req.listuser");
		public static final var REQ_REGISTER: String = get("req.register");
		
		public static final var RSP_SUCCESS: String = get("rsp.success");
		public static final var RSP_ERROR: String = get("rsp.error");
		public static final var RSP_STALE: String = get("rsp.stale");
		public static final var RSP_FORWARD: String = get("rsp.forward");
		
		public static final var OP_CREATE_FEATURE: String = get("op.createFeature");
		public static final var OP_CREATE_RELATIONSHIP: String = get("op.createRelationship");
		public static final var OP_ADD_NAME: String = get("op.addName");
		public static final var OP_ADD_DES: String = get("op.addDes");
		public static final var OP_SET_OPT: String = get("op.setOpt");
		
		public static final var BIN_REL_REFINES: String = get("binrel.refines");
		public static final var BIN_REL_REQUIRES: String = get("binrel.requires");
		public static final var BIN_REL_EXCLUDES: String = get("binrel.excludes");
		
		private static function get(var name: String): String {
			try {
				return protocol.getString(name);
			} catch (var e: Error) {
				return null;
			}
		}

	}
}