package cofm.component.fm
{
	import cofm.model.TreeData;
	
	import mx.utils.StringUtil;
	import mx.controls.treeClasses.*;
	
	public class FeatureTreeItemRenderer extends TreeItemRenderer
	{
		public function FeatureTreeItemRenderer()
		{
			super();
		}
		
		override public function set data(value:Object): void {
			if (value != null) {
				super.data = value;
				var cur: XMLList = new XMLList(TreeListData(super.listData).item);
				if (TreeData.KIND_CLASS == String(cur[0].@kind)) {
					setStyle("textDecoration", "underline");
					setStyle("color", "blue");
					setStyle("fontWeight", "bold");
				} else {
					setStyle("textDecoration", "none");
				
					// Set errors to red color
					var e1: int = int(cur[0].@parents);
					var unnamed: Boolean = String(cur[0].@name) == TreeData.UNNAMED;
					if (e1 > 1 || unnamed) {
						setStyle("color", 0xff0000);
					} else {
						setStyle("color", 0x000000);
					}
					// Bold the controversial features
					var rate: Number = Number(cur[0].@support);
					if (rate < 1) {
						setStyle("fontWeight", 'bold');
					} else {
						setStyle("fontWeight", 'normal');
					}
					
				}
			}
		}
		
		
		// Show controversy rate for controversial features.
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number): void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			if (super.data) {
				var curItems: XMLList = new XMLList(TreeListData(super.listData).item);
				if (TreeData.KIND_OBJECT == String(curItems[0].@kind)) {
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
}