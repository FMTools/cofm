package collab.fm.client.util {
	import mx.resources.ResourceManager;

	[ResourceBundle("ui")]
	public class RS {
		
		public static const FEEDBACK_TITLE: String = s("feedback.title");
		public static const FEEDBACK_INFO: String = s("feedback.info");
		public static const FEEDBACK_LOG: String = s("feedback.log");
		public static const BTN_ISSUE_TRACK: String = s("btn.issuetrack");
		public static const BTN_ISSUE_OTHER: String = s("btn.otherissue");
		
		public static const APP_TITLE: String = s("app.title");

		public static const ERROR_NAME_DUPLICATE: String = s("error.name.duplicate");
		public static const ERROR_NAME_INVALID: String = s("error.name.invalid");
		public static const ERROR_NAME_EMPTY: String = s("error.name.empty");
		public static const ERROR_REL_SELF: String = s("error.rel.self");
		
		public static const BTN_LOGIN: String = s("btn.login");
		public static const BTN_LOGOUT: String = s("btn.logout");
		public static const BTN_REGISTER: String = s("btn.register");
		public static const BTN_BACK_TO_HOME: String = s("btn.backhome");
		public static const BTN_FEEDBACK: String = s("btn.feedback");
		public static const BTN_ADD: String =s("btn.add");
		public static const BTN_OK: String = s("btn.ok");
		public static const BTN_CANCEL: String = s("btn.cancel");
		public static const BTN_RESET: String = s("btn.reset");
		public static const BTN_CREATE: String = s("btn.create");
		public static const BTN_ENTER: String = s("btn.enter");
		public static const BTN_CREATE_FEATURE: String = s("btn.createFeature");
		public static const BTN_CREATE_RELATIONSHIP: String = s("btn.createRelationship");
		public static const BTN_VOTE_YES: String = s("btn.vote.yes");
		public static const BTN_VOTE_NO: String = s("btn.vote.no");
		public static const BTN_VOTE_FEATURE_YES: String = s("btn.vote.feature.yes");
		public static const BTN_VOTE_FEATURE_NO: String = s("btn.vote.feature.no");
		public static const BTN_ADD_NAME: String = s("btn.add.name");
		public static const BTN_ADD_DES: String = s("btn.add.des");
		public static const BTN_VOTE_OPT_YES: String = s("btn.vote.opt.yes");
		public static const BTN_VOTE_OPT_NO: String = s("btn.vote.opt.no");

		public static const DLG_LOGOUT_CONFIRM_TITLE: String = s("dlg.logout.confirm.title");
		public static const DLG_LOGOUT_CONFIRM_TEXT: String = s("dlg.logout.confirm.text");

		public static const CON_PANEL_TITLE: String = s("con.title");
		public static const CON_PANEL_HOST: String = s("con.host");
		public static const CON_PANEL_PORT: String = s("con.port");

		public static const M_LIST_USERS: String = s("mlist.users");
		public static const M_LIST_EMPTY_LIST_INFO: String = s("mlist.empty");
		public static const M_LIST_SUMMARY: String = "mlist.summary";

		public static const M_LIST_CURRENT_NAME: String = s("mlist.current.name");

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

		public static const FEATURE_CREATE_TITLE: String = s("feature.create.title");
		public static const FEATURE_CREATE_NAME: String = s("feature.create.name");
		public static const FEATURE_CREATE_DES: String = s("feature.create.des");
		public static const FEATURE_CREATE_PARENT: String = s("feature.create.parent");
		public static const FEATURE_CREATE_PARENT_CHOOSE: String = s("feature.create.parent.choose");
		public static const FEATURE_CREATE_PARENT_IS_ROOT: String = s("feature.create.parent.isRoot");
		public static const FEATURE_CREATE_PARENT_SEARCH: String = s("feature.create.parent.search");

		public static const REL_CREATE_TITLE: String = s("rel.create.title");
		public static const REL_CREATE_TYPE: String = s("rel.create.type");
		public static const REL_CREATE_PARENT: String = s("rel.create.parent");
		public static const REL_CREATE_CHILD: String = s("rel.create.child");
		public static const REL_CREATE_LEFT: String = s("rel.create.left");
		public static const REL_CREATE_RIGHT: String = s("rel.create.right");
		public static const REL_CREATE_ENTER_PROMPT: String = s("rel.create.enter.prompt");

		public static const REL_TYPE_REFINEMENT: String = "refinement";
		public static const REL_TYPE_REQUIRE: String = "require";
		public static const REL_TYPE_EXCLUDE: String = "exclude";

		public static const TREE_PANEL_TITLE: String = s("tree.panel.title");
		public static const EDIT_PANEL_TITLE: String = s("edit.panel.title");

		public static const EDIT_GRID_YES: String = s("edit.grid.yes");
		public static const EDIT_GRID_NO: String = s("edit.grid.no");
		public static const EDIT_GRID_ACTION: String = s("edit.grid.action");

		public static const EDIT_DES: String = s("edit.des");
		public static const EDIT_DES_GRID_NAME: String = s("edit.des.grid.name");
		public static const EDIT_ADD_DES: String = s("edit.add.des");
		public static const EDIT_ADD_DES_TITLE: String = s("edit.add.des.title");

		public static const EDIT_NAME: String = s("edit.name");
		public static const EDIT_NAME_GRID_NAME: String = s("edit.name.grid.name");

		public static const EDIT_ADD_NAME: String = s("edit.add.name");
		public static const EDIT_ADD_NAME_TITLE: String = s("edit.add.name.title");

		public static const EDIT_FEATURE_VOTES: String = s("edit.feature.votes");
		public static const EDIT_FEATURE_BAR_YES: String = s("edit.feature.bar.yes");
		public static const EDIT_FEATURE_BAR_NO: String = s("edit.feature.bar.no");

		public static const EDIT_REFINE: String = s("edit.refine");
		public static const EDIT_REFINE_PARENT: String = s("edit.refine.parent");
		public static const EDIT_REFINE_CHILD: String = s("edit.refine.child");

		public static const EDIT_REFINE_GRID_NAME: String = s("edit.refine.grid.name");

		public static const EDIT_CONS_GRID_NAME: String = s("edit.cons.grid.name");
		public static const EDIT_CONS: String = s("edit.cons");

		public static const EDIT_TAB_BASIC: String = s("edit.tab.basic");
		public static const EDIT_TAB_NAME: String = s("edit.tab.name");
		public static const EDIT_TAB_DES: String = s("edit.tab.des");
		public static const EDIT_TAB_REFINE: String = s("edit.tab.refine");
		public static const EDIT_TAB_CONS: String = s("edit.tab.cons");
		
		public static const COMMENT_INTRO: String = s("comment.intro");
		public static const COMMENT_TITLE: String = s("comment.title");
		
		public static const OP_LIST_TITLE: String = s("op.list.title");

		private static function s(key: String): String {
			return ResourceManager.getInstance().getString("ui", key);
		}

		public static function format(key: String, param: Array): String {
			return ResourceManager.getInstance().getString("ui", key, param);
		}
	}
}