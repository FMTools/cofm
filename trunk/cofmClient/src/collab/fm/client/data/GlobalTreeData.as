package collab.fm.client.data {
	
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class GlobalTreeData extends TreeData {
		private static var _instance: GlobalTreeData = new GlobalTreeData();

		public static function get instance(): GlobalTreeData {
			return _instance;
		}

		public function GlobalTreeData() {
			super();
		}
		
		override protected function getFeatureName(feature: Object): String {
			// Build an array from the XML.
			if (XMLList(feature.names.name).length() <= 0) {
				return UNNAMED;
			}
			var ns: Array = [];
			for each (var n: Object in feature.names.name) {
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					yes.push(XML(u).text().toString());
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(XML(u1).text().toString());
				}
				ns.push({
						val: n.@val,
						v1: yes,
						v0: no
					});
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance.myId);
			return ns[0].val;
		}
		
		override protected function isPartOfTree(o: Object): Boolean {
			return true;  // Every undeleted element is a part of global tree
		}

		override protected function onDataUpdateComplete(): void {
			Console.info("GlobalTreeData - Tree refreshed.");	
		
		}
		
		override protected function onDataUpdateStart(): void {
		}
	}

}