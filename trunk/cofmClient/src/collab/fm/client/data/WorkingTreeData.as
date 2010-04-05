package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import flash.utils.Dictionary;

	public class WorkingTreeData extends TreeData implements IOperationListener {
		private static var _instance: WorkingTreeData = new WorkingTreeData();

		public static function get instance(): WorkingTreeData {
			return _instance;
		}

		public function WorkingTreeData() {
			super();
			FeatureModel.instance.registerSubView(this);
		}

		public function handleFeatureVotePropagation(op: Object): void {

		}

		public function handleRelationshipVotePropagation(op: Object): void {

		}

		public function handleAddDescription(op:Object): void {

		}

		public function handleAddName(op:Object): void {
			// recalculate the displayed name (primary name)
			var features1: XMLList = ModelUtil.getRootFeatureById(this.xml.source, op["featureId"]);
			var features2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, op["featureId"]);
			var newName: String = this.getPrimaryName(
				XML(FeatureModel.instance.features.source.(@id==op["featureId"])));
			trace("Working tree: new name is '" + newName + "'");
			for each (var f1: Object in features1) {
				if (newName == null) {
					f1.@name = "<unnamed>";
					f1.@unnamed = "1";
				} else if (newName != f1.@name) {
					f1.@name = newName;
				}
			}

			for each (var f2: Object in features2) {
				if (newName == null) {
					f2.@name = "<unnamed>";
					f2.@unnamed = "1";
				} else if (newName != f2.@name) {
					f2.@name = newName;
				}
			}
		}

		public function handleCreateFeature(op:Object): void {
			// TODO: handle deletion
			// if create, add to the root (a creating must assign the root explicitly through the UI.)
			if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
				this.xml.addItem(<feature id={op["featureId"]}
						name={op["value"]} 
						controversy="1" 
						nonPositioned="0"
						multiPositioned="0"
						unnamed="0" />);
			}
			if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
				var features1: XMLList = ModelUtil.getRootFeatureById(this.xml.source, op["featureId"]);
				var features2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, op["featureId"]);

				// Change its children to roots.
				var f: XML = null;
				if (features1.length() > 0) {
					f = features1[0];
				} else if (features2.length() > 0) {
					f = features2[0];
				}
				if (f != null) {
					for each (var child: Object in f.children()) {
						this.xml.addItem(child);
					}
				}

				ModelUtil.deleteRootFeatureById(this.xml, op["featureId"]);
				ModelUtil.deleteNonRootFeatureById(this.xml, op["featureId"]);
			}

			// TODO: handle vote (controversy)
		}

		public function handleCreateBinaryRelationship(op:Object): void {
			// only handles refinements 
			if (op["type"] == Cst.BIN_REL_REFINES) {
				//TODO: handle voting
				// if create
				if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
					var left: String = String(op["leftFeatureId"]);
					var right: String = String(op["rightFeatureId"]);
					var parents1: XMLList = ModelUtil.getRootFeatureById(this.xml.source, left);
					var parents2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, left);
					var children1: XMLList = ModelUtil.getRootFeatureById(this.xml.source, right);
					var children2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, right);

					// 1. remove all children which are root features, and then copy them to parents
					for each (var obj: Object in children1) {
						var childIndex: int = this.xml.getItemIndex(obj);
						this.xml.removeItemAt(childIndex);

						ModelUtil.addChildFeatureToAllParents(parents1, XML(obj), right);
						ModelUtil.addChildFeatureToAllParents(parents2, XML(obj), right);
					}

					// 2. copy all children which are non-root features to parents, and 
					// marked as "Multi-positioned".
					for each (var obj2: Object in children2) {

						obj2.@multiPositioned = "1";
						ModelUtil.addChildFeatureToAllParents(parents1, XML(obj2), right);
						ModelUtil.addChildFeatureToAllParents(parents2, XML(obj2), right);
					}
				}
			}
		}

		public function handleSetOpt(op:Object): void {

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
			if (XMLList(feature.names.name).length() <= 0) {
				return null;
			}

			var ns: Array = [];
			for each (var o: Object in feature.names.name) {
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
			return ns[0].val;
		}

		override protected function onDataUpdateComplete(): void {
			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelUpdateEvent(ModelUpdateEvent.WORKING_VIEW_COMPLETE, null));
		}
	}
}