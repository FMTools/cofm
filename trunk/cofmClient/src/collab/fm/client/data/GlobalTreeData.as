package collab.fm.client.data {
	import collab.fm.client.util.*;

	import flash.utils.Dictionary;

	public class GlobalTreeData extends TreeData {
		private static var _instance: GlobalTreeData = new GlobalTreeData();

		public static function get instance(): GlobalTreeData {
			return _instance;
		}

		public function GlobalTreeData() {
			super();
		}

		override protected function handleAddDescription(op:Object): void {

		}

		override protected function handleAddName(op:Object): void {

		}

		override protected function handleCreateFeature(op:Object): void {
			// if vote, do nothing
			// if create, add the feature to the root
		}

		override protected function handleCreateRelationship(op:Object): void {

		}

		override protected function handleSetOpt(op:Object): void {

		}

		override protected function createSpecificTreeNode(id:String, refines: Dictionary): XML {
			var fs: XMLList = FeatureModel.instance.features.source.(@id==id);
			var yesNum: int = XMLList(fs[0].yes.user).length();
			var noNum: int = XMLList(fs[0].no.user).length();
			var controversyRate: Number = (noNum > 0) ? 1 : (yesNum / (yesNum+noNum));
			return <feature id={id} 
					name={getPrimaryName(fs[0])} 
					controversy={controversyRate} />;
		}

		override protected function addRefinement(refines:Dictionary, r:XML): void {
			var left: String = String(r.@left);
			var right: String = String(r.@right);
			if (refines[left] == null) {
				refines[left] = {parent: [], child: []};
			}
			if (refines[right] == null) {
				refines[right] = {parent: [], child: []};
			}
			(refines[left].child as Array).push(right);
			(refines[right].parent as Array).push(left);
		}

		override protected function isTopNode(id:String, refines:Dictionary): Boolean {
			return refines[id] == null || (refines[id].parent as Array).length <= 0;
		}

		override protected function getPrimaryName(feature: XML): String {
			// Build an array from the XML.
			var ns: Array = [];
			for each (var o: Object in feature.names) {
				var n: XML = o as XML;
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					yes.push(u);
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(u1);
				}
				ns.push({
						val: n.@val,
						v1: yes,
						v0: no
					});
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance.myId);
			return ns[0].val as String;
		}

	}
}