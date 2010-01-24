package collab.fm.client.data {
	import collab.fm.client.util.Cst;

	import mx.collections.XMLListCollection;

	public class User extends AbstractDataView {
		public var myId: int = -1;
		public var myName: String;
		public var currentModelId: int = -1;

		[Bindable]
		public var isLogin: Boolean;

		private static const _defaultXml: XML = <user id="-1" name=""/>;

		// <user id="number" name="string" />
		private var _users: XMLListCollection;

		private static var _instance: User = new User();

		public static function get instance(): User {
			return _instance;
		}

		public function User() {
			super();
			isLogin = false;
			_users = new XMLListCollection(new XMLList(_defaultXml));
		}

		override protected function updateEntireData(input:Object): void {

		}

		override protected function updateMinorChange(input:Object): void {
			switch (input.event) {
				case Cst.DATA_MY_INFO:
					isLogin = true;
					myId = input.myId;
					myName = input.myName;
					break;
				case Cst.DATA_USER_NAMES:
					createUserList(input);
					break;
			}
		}

		private function createUserList(input: Object): void {
			var xml: XML = <users/>;
			for (var key: Object in input.list) {
				xml.appendChild(<user id={int(key)} name={String(input.list[key])} />);
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