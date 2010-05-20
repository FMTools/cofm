package collab.fm.client.ui.renderer {
	import mx.controls.treeClasses.*;
	import mx.utils.StringUtil;
	
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

		
		// Show controversy rate for controversial features.
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number): void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			if (super.data) {
				var curItems: XMLList = new XMLList(TreeListData(super.listData).item);
				var rate: Number = Number(curItems[0].@controversy);
				if (rate < 1) {
					super.label.text = TreeListData(super.listData).label +
						" (" + (rate * 100).toPrecision(3) + "%)";
				}
				var person: String = String(curItems[0].@person);
				if (person != null && mx.utils.StringUtil.trim(person) != "") {
					super.label.text = TreeListData(super.listData).label + "             <----- (" + person + ")";
				}
			}
		}
		
	}
}