package collab.fm.client.data
{
	import collab.fm.client.util.*;
	
	public class PersonalTreeData extends TreeData
	{
		private static var _instance: PersonalTreeData = new PersonalTreeData();

		public static function get instance(): PersonalTreeData {
			return _instance;
		}

		public function PersonalTreeData()
		{
			super();
		}
		
		override protected function getFeatureName(feature: Object): String {
			// Build an array from the XML.
			if (XMLList(feature.names.name).length() <= 0) {
				return UNNAMED;
			}

			var ns: Array = [];
			var onlyOneName: Boolean = XMLList(feature.names.name).length() == 1;
			for each (var n: Object in feature.names.name) {
				if (onlyOneName) {
					if (XMLList(n.no.user.(text().toString()==String(UserList.instance.myId))).length() > 0) {
						return UNNAMED;
					} else {
						return n.@val;
					}
				}
				var nameInTree: Boolean = false;
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					if (UserList.instance.myId == int(u)) {
						yes.push(u);
						nameInTree = true;
					}
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(u1);
				}
				if (nameInTree) {
					ns.push({
							val: n.@val,
							v1: yes,
							v0: no
						});
				}
			}
			if (ns.length == 0) {
				return UNNAMED;
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance.myId);
			return ns[0].val;
		}
		
		override protected function isPartOfTree(o: Object): Boolean {
			//return true is I am a "YES" voter.
			var me: String = String(UserList.instance.myId);
			return XMLList(o.yes.user.(text().toString()==me)).length() > 0;
		}

		override protected function onDataUpdateComplete(): void {
			Console.info("PersonalTreeData - Tree refreshed.");
		}
		
		override protected function onDataUpdateStart(): void {
		}
		
	}
}