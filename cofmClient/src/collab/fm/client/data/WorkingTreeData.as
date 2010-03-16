package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import flash.utils.Dictionary;

	public class WorkingTreeData extends TreeData {
		private static var _instance: WorkingTreeData = new WorkingTreeData();

		public static function get instance(): WorkingTreeData {
			return _instance;
		}

		public function WorkingTreeData() {
			super();
		}

		override protected function handleAddDescription(op:Object): void {

		}

		override protected function handleAddName(op:Object): void {

		}

		override protected function handleCreateFeature(op:Object): void {
			// if vote, do nothing
			// if create, add to the root (a creating must assign the root explicitly through the UI.)
		}

		override protected function handleCreateRelationship(op:Object): void {

		}

		override protected function handleSetOpt(op:Object): void {

		}

		override protected function createSpecificTreeNode(id:String, refines: Dictionary): XML {
			var fs: XMLList = FeatureModel.instance.features.source.(@id==id);
			for each (var item: Object in fs[0].no.user) {
				if (UserList.instance.myId == int(item)) {
					return null; // this feature should not include in my working view.
				}
			}
			var yesNum: int = XMLList(fs[0].yes.user).length();
			var noNum: int = XMLList(fs[0].no.user).length();
			var _controversyRate: Number = (noNum > 0) ? 1 : (yesNum / (yesNum+noNum));
			// NOTE: do not use Boolean for XML attributes
			var _nonPositioned: int = 0;
			var _multiPositioned: int = 0;
			var _unnamed: int = 0;
			var _name: String = getPrimaryName(fs[0]);
			if (_name == null) {
				_name = "<unnamed>";
				_unnamed = 1;
			}
			if (refines[id] != null && refines[id].parent == null) {
				_nonPositioned = 1;
			}
			if (refines[id] != null && refines[id].parent != null && 
				(refines[id].parent as Array).length > 1) {
				_multiPositioned = 1;
			}
			return <feature id={id} 
					name={_name} 
					controversy={_controversyRate} 
					nonPositioned={_nonPositioned}
					multiPositioned={_multiPositioned}
					unnamed={_unnamed}/>;
		}

		override protected function addRefinement(refines:Dictionary, r:XML): void {
			var validRefinement: Boolean = true;
			for each (var item: Object in r.no.user) {
				if (UserList.instance.myId == int(item)) {
					validRefinement = false;
				}
			}
			var left: String = String(r.@left);
			var right: String = String(r.@right);
			if (refines[left] == null && validRefinement) {
				refines[left] = {parent: [], child: []};
			}
			if (refines[right] == null) {
				if (validRefinement) {
					refines[right] = {parent: [], child: []};
				} else {
					refines[right] = {parent: null, child: []};
				}
			}
			if (validRefinement) {
				(refines[left].child as Array).push(right);
				if (refines[right].parent == null) {
					refines[right].parent = [];
				}
				(refines[right].parent as Array).push(left);
			}
		}

		override protected function isTopNode(id:String, refines:Dictionary): Boolean {
			return refines[id] == null ||
				refines[id].parent == null ||
				(refines[id].parent as Array).length <= 0;
		}

		override protected function getPrimaryName(feature: XML): String {
			// Build an array from the XML.
			var ns: Array = [];
			for each (var o: Object in feature.names) {
				var nameInMyWorkingView: Boolean = true;
				var n: XML = o as XML;
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					yes.push(u);
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(u1);
					if (UserList.instance.myId == int(u1)) {
						nameInMyWorkingView = false;
					}
				}
				if (nameInMyWorkingView) {
					ns.push({
							val: n.@val,
							v1: yes,
							v0: no
						});
				}
			}
			if (ns.length == 0) {
				return null;
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance.myId);
			return ns[0].val as String;
		}

		override protected function onDataUpdateComplete(): void {
			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelUpdateEvent(ModelUpdateEvent.WORKING_VIEW_COMPLETE, null));
		}
	}
}