package collab.fm.client.data {
	import mx.collections.XMLListCollection;

	import collab.fm.client.util.Cst;

	public class User extends AbstractDataView {
		public var myId: int = -1;
		public var myName: String;
		public var isLogin: Boolean;

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