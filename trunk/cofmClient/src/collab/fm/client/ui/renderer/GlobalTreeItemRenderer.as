package collab.fm.client.ui.renderer {
	import mx.controls.treeClasses.*;

	public class GlobalTreeItemRenderer extends TreeItemRenderer {
		public function GlobalTreeItemRenderer() {
			super();
		}

		override public function set data(value:Object): void {
			if (value != null) {
				super.data = value;
				var curItems: XMLList = new XMLList(TreeListData(super.listData).item);
				// Bold the controversial features
				if (Number(curItems[0].@controversy) < 1) {
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
				var rate: Number = Number(curItems[0].@controversy);
				if (rate < 1) {
					super.label.text = TreeListData(super.listData).label +
						" (" + (rate * 100).toPrecision(3) + "%)";
				}
			}
		}
	}
}