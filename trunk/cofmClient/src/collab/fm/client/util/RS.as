package collab.fm.client.util {
	import mx.resources.ResourceManager;

	[ResourceBundle("ui")]
	public class RS {

		public static const BTN_OK: String = s("btn.ok");
		public static const BTN_CANCEL: String = s("btn.cancel");
		public static const BTN_CREATE: String = s("btn.create");

		public static const CON_PANEL_TITLE: String = s("con.title");
		public static const CON_PANEL_HOST: String = s("con.host");
		public static const CON_PANEL_PORT: String = s("con.port");

		public static const M_LIST_USERS: String = s("mlist.users");
		public static const M_LIST_EMPTY_LIST_INFO: String = s("mlist.empty");
		public static const M_LIST_SUMMARY: String = "mlist.summary";

		public static const LOGIN_HEADING: String = s("login.heading");
		public static const LOGIN_INPUT_NAME: String = s("login.name");
		public static const LOGIN_INPUT_PWD: String = s("login.pwd");
		public static const LOGIN_GO_REGISTER: String = s("login.gotoRegister");
		public static const LOGIN_REGISTER_BTN: String = s("login.link.reg");

		public static const REG_HEADING: String = s("reg.heading");
		public static const REG_NAME: String = s("reg.name");
		public static const REG_PWD: String = s("reg.pwd");
		public static const REG_PWD_CONFIRM: String = s("reg.pwd2");
		public static const REG_PWD_CONFIRM_ERROR: String = s("reg.pwd.confirm.error");

		public static const MODEL_SEARCH_DEFAULT: String = s("model.search.default");

		public static const MODEL_CREATE_INTRO: String = "model.create.intro";
		public static const MODEL_CREATE_NAME: String = s("model.create.name");
		public static const MODEL_CREATE_DESCRIPTION: String = s("model.create.description");
		public static const MODEL_CREATE_NOLOGIN_INTRO: String = "model.create.noLoginIntro";

		private static function s(key: String): String {
			return ResourceManager.getInstance().getString("ui", key);
		}

		public static function format(key: String, param: Array): String {
			return ResourceManager.getInstance().getString("ui", key, param);
		}
	}
}