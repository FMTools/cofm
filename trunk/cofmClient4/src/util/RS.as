package util
{
	import mx.resources.ResourceManager;
	
	/**
	 * Locale resources
	 */
	[ResourceBundle("ui")]
	public class RS {
		
		# Common
		public static const ok: String = s("ok");
		public static const cancel: String = s("cancel");
		public static const close: String = s("close");
		public static const reset: String = s("reset");
		public static const submit: String = s("submit");
		public static const go: String = s("go");
		
		public static const go_login: String = s("go_login");
		public static const go_register: String = s("go_register");
		public static const go_logout: String = s("go_logout");
		public static const go_home: String = s("go_home");
		public static const go_feedback: String = s("go_feedback");
		public static const go_bug_page: String = s("go_bug_page");
		
		public static const feedback_title: String = s("feedback_title");
		public static const feedback_info: String = s("feedback_info");
		public static const feedback_log: String = s("feedback_log");
		public static const feedback_close: String = s("feedback_close");
		
		public static const con_title: String = s("con_title");
		public static const con_host: String = s("con_host");
		public static const con_port: String = s("con_port");
		
		public static const fmlist_users: String = s("fmlist_users");
		public static const fmlist_enter: String = s("fmlist_enter");
		public static const fmlist_empty: String = s("fmlist_empty");
		public static const fmlist_current_name: String = s("fmlist_current_name");
		public static const fmlist_summary: String = s("fmlist_summary");
		public static const fmlist_search_prompt: String = s("fmlist_search_prompt");
		public static const fmlist_new_intro: String = s("fmlist_new_intro");
		public static const fmlist_new_needlogin: String = s("fmlist_new_needlogin");
		public static const fmlist_new_name: String = s("fmlist_new_name");
		public static const fmlist_new_description: String = s("fmlist_new_description");
		
		public static const login_heading: String = s("login_heading");
		public static const login_name: String = s("login_name");
		public static const login_pwd: String = s("login_pwd");
		public static const login_new: String = s("login_new");
		public static const login_go_register: String = s("login_go_register");
		
		public static const reg_heading: String = s("reg_heading");
		public static const reg_name: String = s("reg_name");
		public static const reg_pwd: String = s("reg_pwd");
		public static const reg_pwd2: String = s("reg_pwd2");
		public static const reg_error_pwd_confirm: String = s("reg_error_pwd_confirm");
		
		public static const logout_title: String = s("logout_title");
		public static const logout_info: String = s("logout_info");
		
		public static const m_new_f: String = s("m_new_f");
		public static const m_new_r: String = s("m_new_r");
		public static const m_new_a: String = s("m_new_a");
		public static const m_new_v: String = s("m_new_v");
		public static const m_add: String = s("m_add");
		public static const m_yes: String = s("m_yes");
		public static const m_no: String = s("m_no");
		public static const m_yes_f: String = s("m_yes_f");
		public static const m_no_f: String = s("m_no_f");

		public static const m_tree_title: String = s("m_tree_title");
		
		public static const m_cf_title: String = s("m_cf_title");
		public static const m_cf_name: String = s("m_cf_name");
		public static const m_cf_des: String = s("m_cf_des");
		public static const m_cf_root: String = s("m_cf_root");
		public static const m_cf_parent: String = s("m_cf_parent");
		public static const m_cf_select: String = s("m_cf_select");
		
		public static const m_cr_title: String = s("m_cr_title");
		public static const m_cr_type: String = s("m_cr_type");
		public static const m_cr_parent: String = s("m_cr_parent");
		public static const m_cr_child: String = s("m_cr_child");
		public static const m_cr_prompt: String = s("m_cr_prompt");
		public static const m_cr_left: String = s("m_cr_left");
		public static const m_cr_right: String = s("m_cr_right");
		public static const m_cr_error_self: String = s("m_cr_error_self");
		
		public static const m_ca_title: String = s("m_ca_title");
		public static const m_ca_cur: String = s("m_ca_cur");
		public static const m_ca_name: String = s("m_ca_name");
		public static const m_ca_type: String = s("m_ca_type");
		public static const m_ca_heading: String = s("m_ca_heading");
		public static const m_ca_enum: String = s("m_ca_enum");
		public static const m_ca_range: String = s("m_ca_range");
		public static const m_ca_unit: String = s("m_ca_unit");
		public static const m_ca_add_row: String = s("m_ca_add_row");
		
		public static const m_cv_title: String = s("m_cv_title");
		public static const m_cv_attr: String = s("m_cv_attr");
		public static const m_cv_value: String = s("m_cv_value");
		
		public static const m_fe_title: String = s("m_fe_title");
		
		public static const m_fe_basic_tab: String = s("m_fe_basic_tab");
		public static const m_fe_basic_votes: String = s("m_fe_basic_votes");
		public static const m_fe_basic_votes_yes: String = s("m_fe_basic_votes_yes");
		public static const m_fe_basic_votes_no: String = s("m_fe_basic_votes_no");
		
		public static const m_fe_grid_value: String = s("m_fe_grid_value");
		public static const m_fe_grid_yes: String = s("m_fe_grid_yes");
		public static const m_fe_grid_no: String = s("m_fe_grid_no");
		public static const m_fe_grid_action: String = s("m_fe_grid_action");
		
		public static const m_fe_number_unit: String = s("m_fe_number_unit");
		public static const m_fe_number_min: String = s("m_fe_number_min");
		public static const m_fe_number_max: String = s("m_fe_number_max");
		
		public static const m_fe_refine_tab: String = s("m_fe_refine_tab");
		public static const m_fe_refine_parent: String = s("m_fe_refine_parent");
		public static const m_fe_refine_child: String = s("m_fe_refine_child");
		public static const m_fe_refine_grid_name: String = s("m_fe_refine_grid_name");
		
		public static const m_fe_cons_tab: String = s("m_fe_cons_tab");
		public static const m_fe_cons_grid_name: String = s("m_fe_cons_grid_name");
		
		public static const m_comment_intro: String = s("m_comment_intro");
		public static const m_comment_title: String = s("m_comment_title");
		
		public static const m_history_title: String = s("m_history_title");
		
		public static const m_error_duplicate_name: String = s("m_error_duplicate_name");
		public static const m_error_invalid_name: String = s("m_error_invalid_name");
		public static const m_error_empty_name: String = s("m_error_empty_name");
		
		private static function s(key: String): String {
			return ResourceManager.getInstance().getString("ui", key);
		}
		
		public static function format(key: String, param: Array): String {
			return ResourceManager.getInstance().getString("ui", key, param);
		}
	}
}