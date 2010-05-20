package collab.fm.client.data {
	
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.controls.Alert;

	public class GlobalTreeData extends TreeData implements IOperationListener {
		private static var _instance: GlobalTreeData = new GlobalTreeData();

		public static function get instance(): GlobalTreeData {
			return _instance;
		}

		public function GlobalTreeData() {
			super();
			FeatureModel.instance.registerSubView(this);
			
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.LOCAL_MODEL_COMPLETE, onLocalModelUpdate);
				
			
				
			Console.info("GlobalTreeData - ctor");
		}
		
		
		private function onLocalModelUpdate(evt: ModelUpdateEvent): void {
			refreshData(evt);
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
			trace("Global Tree: new name is '" + newName + "'");
			for each (var f1: Object in features1) {
				if (newName == null) {
					f1.@name = "<unnamed>";
				} else if (newName != f1.@name) {
					f1.@name = newName;
				}
			}

			for each (var f2: Object in features2) {
				if (newName == null) {
					f2.@name = "<unnamed>";
				} else if (newName != f2.@name) {
					f2.@name = newName;
				}
			}
		}

		public function handleCreateFeature(op:Object): void {
			// if create, add the feature to the root
			if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
				this.xml.addItem(<feature id={op["featureId"]}
						name={op["value"]} controversy="1" person="" />);
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
				ModelUtil.removeRootFeatureById(this.xml, op["featureId"]);
				ModelUtil.removeNonRootFeatureById(this.xml, op["featureId"]);
			}
		}

		public function handleCreateBinaryRelationship(op:Object): void {
			// The tree only deals with refinement relationships.
			if (op["type"] == Cst.BIN_REL_REFINES) {
				var left: String = String(op["leftFeatureId"]);
				var right: String = String(op["rightFeatureId"]);
				var parents1: XMLList = ModelUtil.getRootFeatureById(this.xml.source, left);
				var parents2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, left);
				var children1: XMLList = ModelUtil.getRootFeatureById(this.xml.source, right);
				var children2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, right);

				if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
					var childCopy: XML = null;
					if (children1.length() > 0) {
						childCopy = children1[0];
					} else if (children2.length() > 0) {
						childCopy = children2[0];
					}
					if (childCopy == null) {
						//SHOULD NEVER REACH HERE!!

						Alert.show(
							"Something bad has happend!!\n" +
							"If you are a user of this tool, please click the 'Feedback' button and " +
							"copy and send the following message to the developer (sorry for any inconvenience):\n\t" +
							"BUG: we should never reach here (in GlobalTreeData.as, function handleCreateBinaryRelationship).",
							"Oh no!!!");
						return;
					}
					// First, remove the children from its parents.
					ModelUtil.removeChildFeatureFromAllParents(parents1, right);
					ModelUtil.removeChildFeatureFromAllParents(parents2, right);

					// If there's no feature with "rightId" here, then the children have no other parents,
					// and they should be set as root features.
					var rightFeature1: XMLList = ModelUtil.getRootFeatureById(this.xml.source, right);
					var rightFeature2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, right);
					if (rightFeature1.length() <= 0 && rightFeature2.length() <= 0) {
						// Set childCopy as the root
						this.xml.addItem(childCopy.copy());
					}
				}

				if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
					// if create, then:
					//		if the child has already had another parent, COPY the child to the parent in the op.
					//		else, MOVE the child to the parent in op (copy then delete).


					// 1. remove all children which are root features, and then copy them to parents
					for each (var obj: Object in children1) {
						var childIndex: int = this.xml.getItemIndex(obj);
						this.xml.removeItemAt(childIndex);

						ModelUtil.addChildFeatureToAllParents(parents1, XML(obj), right);
						ModelUtil.addChildFeatureToAllParents(parents2, XML(obj), right);
					}

					// 2. copy all children which are non-root features to parents
					for each (var obj2: Object in children2) {
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
			var yesNum: int = XMLList(fs[0].yes.user).length();
			var noNum: int = XMLList(fs[0].no.user).length();
			var controversyRate: Number = (noNum > 0) ? 1 : (yesNum / (yesNum+noNum));
			var fname: String = getPrimaryName(fs[0]);
			if (fname == null) {
				fname = "<unnamed>";
			}
			return <feature id={id} 
					name={fname} 
					controversy={controversyRate}
					person="" />;
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
			if (XMLList(feature.names.name).length() <= 0) {
				return null;
			}
			var ns: Array = [];
			for each (var o: Object in feature.names.name) {
				var n: XML = o as XML;
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					yes.push(XML(u).text().toString());
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(XML(u1).text().toString());
				}
				ns.push({
						val: n.@val,
						v1: yes,
						v0: no
					});
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance.myId);
			return ns[0].val;
		}

		override protected function onDataUpdateComplete(): void {
			Console.info("GlobalTreeData - Model refreshed. Tree completed.");	
		
		}
		
		override protected function onDataUpdateStart(): void {
			Console.info("GlobalTreeData - Tree refresh starting...");
		}
	}

}