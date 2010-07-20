package collab.fm.client.ui.renderer {
	import mx.controls.Button;
	import mx.controls.treeClasses.*;
	import mx.utils.StringUtil;

	public class GlobalTreeItemRenderer extends TreeItemRenderer {
		public function GlobalTreeItemRenderer() {
			super();
		}

		override public function set data(value:Object): void {
			if (value != null) {
				super.data = value;
				var curItems: XMLList = new XMLList(TreeListData(super.listData).item);
				// Bold the controversial features
				var rate: Number = Number(curItems[0].@support);
				
				if (rate < 1) {
					setStyle("fontWeight", 'bold');
				} else {
					setStyle("fontWeight", 'normal');
				}
			}
		}

		// Show controversy rate for controversial features.
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number): void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			if (super.data) {
				var curItems: XMLList = new XMLList(TreeListData(super.listData).item);
				var extraInfo: String = "";
				var rate: Number = Number(curItems[0].@support);
				if (rate < 1) {
					extraInfo += " (" + (rate * 100).toPrecision(3) + "%)";
				}
				var people: String = String(curItems[0].@people);
				if (people != null && mx.utils.StringUtil.trim(people) != "") {
					extraInfo += "    <----- (" + people + ")";
				}
				super.label.text = TreeListData(super.listData).label + extraInfo;
			}
		}
	}
}