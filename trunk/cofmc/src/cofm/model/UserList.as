package cofm.model {
	import cofm.event.*;
	
	import mx.collections.XMLListCollection;
	
	import cofm.util.*;

	public class UserList {
		public var myId: int = -1;
		public var myName: String;

		[Bindable]
		public var isLogin: Boolean;

		private static const _defaultBinding: XML = <user id="-1" name=""/>;
		// <user id="number" name="string" />
		[Bindable]
		public var users: XMLListCollection;

		private static var _instance: UserList = new UserList();

		public static function instance(): UserList {
			return _instance;
		}

		public function UserList() {
			isLogin = false;
			users = new XMLListCollection(new XMLList(_defaultBinding));

			ClientEvtDispatcher.instance().addEventListener(ListUserEvent.SUCCESS, onListUser);
			ClientEvtDispatcher.instance().addEventListener(LoginEvent.SUCCESS, onLogin);
			ClientEvtDispatcher.instance().addEventListener(LogoutEvent.LOGGED_OUT, onLogout);
			ClientEvtDispatcher.instance().addEventListener(ListUserEvent.APPEND, onAppendUser);
		}
		
		public function getNameById(id: int): String {
			var subject: String;
				var u: XMLList = this.users.source.(@id==String(id));
				if (u.length() > 0) {
					subject = u[0].@name;
				} else {
					subject = "User #" + id;
				}
				return subject;
		}
		
		private function onLogout(evt: LogoutEvent): void {
			isLogin = false;
			myId = -1;
			myName = "";
		}
		
		private function onLogin(evt: LoginEvent): void {
			isLogin = true;
			myId = evt.myId;
			myName = evt.myName;
		}

		private function onListUser(evt: ListUserEvent): void {
			var xml: XML = <users/>;
			for (var key: Object in evt.users) {
				xml.appendChild(<user id={int(key)} name={String(evt.users[key])} />);
			}
			users.removeAll();
			users.source = xml.user;
			ClientEvtDispatcher.instance().dispatchEvent(new ListUserEvent(ListUserEvent.LOCAL_COMPLETE, null));
		}
		
		private function onAppendUser(evt: ListUserEvent): void {
			for (var key: Object in evt.users) {
				users.addItem(<user id={int(key)} name={String(evt.users[key])} />);
			}
		}
	}
}