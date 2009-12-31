package collab.fm.client.data {
	import collab.fm.client.util.Cst;

	import mx.collections.XMLListCollection;

	public class User extends AbstractDataView {
		public var myId: int = -1;
		public var myName: String;
		public var currentModelId: int = -1;

		[Bindable]
		public var isLogin: Boolean;

		// <user id="number" name="string" />
		private var _users: XMLListCollection;

		private static var _instance: User = new User();

		public static function get instance(): User {
			return _instance;
		}

		public function User() {
			isLogin = false;
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
			users = new XMLListCollection();
			for (var key: Object in input.list) {
				users.addItem(<user id={int(key)} name={String(input.list[key])} />);
			}
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