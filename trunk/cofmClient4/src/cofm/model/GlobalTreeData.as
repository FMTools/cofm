package cofm.model
{
	import cofm.event.*;
	import cofm.util.*;
	
	public class GlobalTreeData extends TreeData {
		private static var _instance: GlobalTreeData = new GlobalTreeData();
		
		public static function instance(): GlobalTreeData {
			return _instance;
		}
		
		public function GlobalTreeData() {
			super();
		}
		
		override protected function getFeatureDisplayName(feature: Object): String {
			var allNames: XMLList = FeatureModel.instance().getValuesOfAttr(XML(feature), Cst.ATTR_FEATURE_NAME);
			if (allNames.length() <= 0) {
				return UNNAMED;
			}
			// Build an array to sort.
			var ns: Array = [];
			for each (var n: Object in allNames) {
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					yes.push(XML(u).text().toString());
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(XML(u1).text().toString());
				}
				ns.push({
					val: XML(n.str).text().toString(),
					v1: yes,
					v0: no
				});
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance().myId);
			return ns[0].val;
		}
		
		override protected function isPartOfTree(o: Object): Boolean {
			return true;  // Every element is a part of global tree
		}
		
		override protected function onDataUpdateComplete(): void {
			
		}
		
		override protected function onDataUpdateStart(): void {
		}
	}
}