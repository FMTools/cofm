package cofm.model
{
	import cofm.event.*;
	import cofm.util.*;
	
	public class WorkingTreeData extends TreeData implements IOperationListener {
		private static var _instance: WorkingTreeData = new WorkingTreeData();
		
		public static function instance(): WorkingTreeData {
			return _instance;
		}
		
		public function WorkingTreeData() {
			super();
		}
		
		override protected function getFeatureDisplayName(feature: Object): String {
			var allNames: XMLList = FeatureModel.instance().getValuesOfAttr(XML(feature), Cst.ATTR_FEATURE_NAME);
			if (allNames.length() <= 0) {
				return UNNAMED;
			}
			
			// Build an array from the XML.
			var ns: Array = [];
			for each (var n: Object in allNames) {
				var nameInMyWorkingView: Boolean = true;
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					yes.push(u);
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(u1);
					if (UserList.instance().myId == int(u1)) {
						nameInMyWorkingView = false;
					}
				}
				if (nameInMyWorkingView) {
					ns.push({
						val: XML(n.str).text().toString(),
						v1: yes,
						v0: no
					});
				}
			}
			if (ns.length == 0) {
				return UNNAMED;
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance().myId);
			return ns[0].val;
		}
		
		override protected function isPartOfTree(o: Object): Boolean {
			//return true is I am not a "NO" voter
			var me: String = String(UserList.instance().myId);
			return XMLList(o.no.user.(text().toString()==me)).length() <= 0;
		}
		
		override protected function onDataUpdateComplete(): void {
			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelUpdateEvent(ModelUpdateEvent.WORKING_VIEW_COMPLETE, null));
		}
		
		override protected function onDataUpdateStart(): void {
		}
	}
}