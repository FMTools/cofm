package collab.fm.client.data {
	import collab.fm.client.event.*;

	import mx.collections.XMLListCollection;

	public class UserList {
		public var myId: int = -1;
		public var myName: String;

		[Bindable]
		public var isLogin: Boolean;

		private static const _defaultBinding: XML = <user id="-1" name=""/>;
		// <user id="number" name="string" />
		private var _users: XMLListCollection;

		private static var _instance: UserList = new UserList();

		public static function get instance(): UserList {
			return _instance;
		}

		public function UserList() {
			isLogin = false;
			_users = new XMLListCollection(new XMLList(_defaultBinding));

			ClientEvtDispatcher.instance().addEventListener(ListUserEvent.SUCCESS, onListUser);
			ClientEvtDispatcher.instance().addEventListener(LoginEvent.SUCCESS, onLogin);

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
		}

		[Bindable]
		public function get users(): XMLListCollection {
			return _users;
		}

		public function set users(xml: XMLListCollection): void {
			_users = xml;
		}
	}
}