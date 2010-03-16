package collab.fm.client.ui.renderer {
	import mx.controls.treeClasses.*;

	public class WorkingTreeItemRenderer extends GlobalTreeItemRenderer {
		public function WorkingTreeItemRenderer() {
			super();
		}

		override public function set data(value:Object): void {
			if (value != null) {
				super.data = value;
				var cur: XMLList = new XMLList(TreeListData(super.listData).item);
				// Set errors to red color
				var e1: int = int(cur[0].@nonPositioned);
				var e2: int = int(cur[0].@multiPositioned);
				var e3: int = int(cur[0].@unnamed);
				if (e1 + e2 + e3 > 0) {
					setStyle("color", 0xff0000);
				}
			}
		}
	}
}