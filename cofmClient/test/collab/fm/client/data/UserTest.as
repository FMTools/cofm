package collab.fm.client.data {
	import flash.utils.Dictionary;

	import collab.fm.client.util.*;

	import flexunit.framework.TestCase;

	public class UserTest extends TestCase {
		public function testUpdateUserList(): void {
			var input: Dictionary = new Dictionary();
			input["1"] = "ronnie";
			input["2"] = "higgins";
			input["3"] = "maguire";
			User.instance.refresh({
					"event": Cst.DATA_USER_NAMES,
					"list": input
				}, true);
			trace("----------------- User.updateUserList --------------");
			trace(User.instance.users.toXMLString());
			trace("--------------------------------------");
		}
	}
}