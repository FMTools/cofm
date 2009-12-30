package collab.fm.client.util {
	import mx.resources.ResourceManager;


	public class RS {

		public static var BTN_OK: String = s("btn.ok");

		public static var CON_PANEL_TITLE: String = s("con.title");
		public static var CON_PANEL_HOST: String = s("con.host");
		public static var CON_PANEL_PORT: String = s("con.port");

		public static var M_LIST_USERS: String = s("mlist.users");
		public static var M_LIST_EMPTY_LIST_INFO: String = s("mlist.empty");

		public static var LOGIN_HEADING: String = s("login.heading");
		public static var LOGIN_INPUT_NAME: String = s("login.name");
		public static var LOGIN_INPUT_PWD: String = s("login.pwd");
		public static var LOGIN_GO_REGISTER: String = s("login.gotoRegister");
		public static var LOGIN_REGISTER_BTN: String = s("login.link.reg");

		public static var REG_HEADING: String = s("reg.heading");

		private static function s(key: String): String {
			return ResourceManager.getInstance().getString("ui", key);
		}
	}
}