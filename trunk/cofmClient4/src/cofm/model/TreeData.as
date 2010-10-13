package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.collections.XMLListCollection;
	
	public class TreeData implements IOperationListener {
		
		/** The XML of tree
		 *  <feature>
		 *      <feature ... >
		 * </feature>
		 *  Tree nodes:
		 *      <feature id= creator= time= support= 
		 *                  parents={number of parents} name= people={people who are editing on me}>
		 *          <feature .../>
		 *      </feature>
		 */
		[Bindable] public var xml: XMLListCollection;
		
		protected var refinements: Dictionary = new Dictionary();
		
		public static const UNNAMED: String = "<unnamed>";
		
		public function TreeData() {
			this.xml = new XMLListCollection(new XMLList(<feature/>));
			
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.OTHER_PEOPLE_SELECT_ON_TREE, onOtherPeopleSelect);
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.LOCAL_MODEL_COMPLETE, onLocalModelUpdate);
			ClientEvtDispatcher.instance().addEventListener(
				LogoutEvent.LOGGED_OUT, onPeopleLogout);
			ClientEvtDispatcher.instance().addEventListener(
				PageSwitchEvent.OTHERS_EXIT_WORK_PAGE, onPeopleExitModel);
			
			FeatureModel.instance().registerSubView(this);
		}
		
		//--------------------------------------------------
		//   Tree Manipulation
		//--------------------------------------------------
		protected function get root(): XML {
			return XML(this.xml.getItemAt(0));
		}
		
		protected function getFeatureById(id: String): XMLList {
			return this.root..feature.(@id==id);		
		}
		
		protected function createFeature(id: String): XML {
			var fs: XMLList = FeatureModel.instance().features.source.(@id==id);
			if (fs.length() <= 0 || !isPartOfTree(fs[0])) {
				return null;
			}
			var name: String = getFeatureDisplayName(fs[0]);
			return <feature id={id} 
				creator={fs[0].@creator}
				time={fs[0].@time}
				support={FeatureModel.instance().getSupportRate(fs[0])}
				parents="0"
				name={name}
				people="" />; 
		}
		
		protected function checkAndAddFeature(id: String): void {
			if (!this.containsFeature(id)) {
				var f: XML = this.createFeature(id);
				if (f != null) {
					this.root.appendChild(f);
				}
			}
		}
		
		protected function removeFeatureById(id: String): void {
			var targets: XMLList = getFeatureById(id);
			// for all children of targets, we should remove refinement of them first.
			// After that, we remove the targets.
			if (targets.length() > 0) {
				var p: XML = targets[0];
				for each (var c: Object in p.children()) {
					removeRefinement(FeatureModel.instance().getRefinementId(p.@id, c.@id));
				}
				ModelUtil.clearXMLList(targets);
			}
		}
		
		protected function addRefinement(id: String): void {
			if (this.refinements[id] == true) {
				return;
			}
			var rs: XMLList = FeatureModel.instance().binaries.source.(@id==id);
			if (rs.length() <= 0 || rs[0].@type != Cst.BIN_REL_REFINES || !isPartOfTree(rs[0])) {
				return;
			}
			var parent: String = rs[0].@left;
			var child: String = rs[0].@right;
			var p: XMLList = this.getFeatureById(parent);
			var c: XMLList = this.getFeatureById(child);
			
			checkEmptyList(p, parent);
			checkEmptyList(c, child);
			
			if (c.length() > 0 && p.length() > 0) {
				// if c.parents == 0, c is a root feature, then we move c to p (by deleting c first, and copy c to p later.)
				// else c is already a child feature, then we just need to copy c to p;
				// in both cases we increase c.parents by 1.
				var ch: XML = XML(c[0]).copy();
				if (c[0].@parents == "0") {
					ModelUtil.clearXMLList(c);
				}
				var hasNewParent: Boolean = false;
				for each (var pa: Object in p) {
					// if ch is not pa's child yet
					if (XMLList(pa.feature.(@id==ch.@id)).length() <= 0) {
						hasNewParent = true;
						XML(pa).appendChild(ch.copy());
					}
				}
				if (hasNewParent) {
					// Increase child.parents by 1.
					for each (var chd: Object in this.getFeatureById(child)) {
						chd.@parents = int(chd.@parents) + 1;
					}
				}
				// Record in refinements dictionary
				this.refinements[id] = true;
			}
		}
		
		/**
		 * If parentId == null or childId == null, then this method will get relationship data from the feature model.
		 */
		protected function removeRefinement(id: String, parentId: String=null, childId: String=null): void {
			if (id == null || this.refinements[id] != true) {
				return;
			}
			
			var parent: String;
			var child: String;
			if (parentId == null || childId == null) {
				var rs: XMLList = FeatureModel.instance().binaries.source.(@id==id);
				if (rs.length() <= 0 || rs[0].@type != Cst.BIN_REL_REFINES) {
					return;
				}
				parent = rs[0].@left;
				child = rs[0].@right;
			} else {
				parent = parentId;
				child = childId;
			}
			
			var p: XMLList = this.getFeatureById(parent);
			var c: XMLList = this.getFeatureById(child);
			
			if (c.length() > 0 && p.length() > 0) {
				var ch: XML = XML(c[0]).copy();
				
				// if ch.parents == 0 (ch is a root feature), then we do noting.
				// if ch.parents == 1, we remove c from p, and then make c a root feature
				// otherwise we just remove c from p
				// in the later two cases, we decrease ch.parents by 1
				if (ch.@parents == "0") {
					return;
				}
				for each (var pa: Object in p) {
					ModelUtil.clearXMLList(XMLList(pa.feature.(@id==ch.@id)));
				}
				if (ch.@parents == "1") {  // make ch a root feature
					this.root.appendChild(ch);
				}
				for each (var chd: Object in this.getFeatureById(child)) {
					chd.@parents = int(chd.@parents) - 1;
				}
				
				// Remove refinement from dictionary
				delete this.refinements[id];
			}
		}
		
		private function checkEmptyList(list: XMLList, id: String): void {
			if (list.length() <= 0) {
				var node: XML = this.createFeature(id);
				if (node != null) {
					this.root.appendChild(node);
					list[0] = node;
				}
			}
		}
		
		protected function addPersonLocation(id: String, person: String): void {
			for each (var feature: Object in getFeatureById(id)) {
				var people: String = feature.@people;
				if (people != "") {
					people += ", ";   // append a person to other people.
				}
				people += person;
				feature.@people = people;
			}
		}
		
		protected function removePersonLocation(person: String): void {
			for each (var feature: Object in this.root..feature) {	
				var people: Array = String(feature.@people).split(/,\s*/);
				var rslt: String = "";
				var notEmpty: Boolean = false;
				for (var i: int = 0; i < people.length; i++) {
					if (people[i] == person) {
						continue;    // skip this person
					}
					if (notEmpty) {
						rslt += ", ";
					}
					rslt += people[i];    // keep other people
					notEmpty = true;
				}
				feature.@people = rslt;
			}
		}
		
		protected function updateFeatureSupportRate(id: String): void {
			var sr: Number = FeatureModel.instance().getFeatureSupportRate(id);
			for each (var f: Object in this.getFeatureById(id)) {
				f.@support = sr;
			}
		}
		
		/**
		 * Clear and re-build the tree
		 */
		protected function refresh(): void {
			onDataUpdateStart();
			
			this.refinements = new Dictionary();
			
			this.xml = new XMLListCollection(new XMLList(<feature/>));
			
			for each (var f: Object in FeatureModel.instance().features.source) {
				var feature: XML = this.createFeature(f.@id);
				if (feature != null) {
					this.root.appendChild(feature);
				}
			}	
			for each (var r: Object in FeatureModel.instance().binaries.source) {
				if (r.@type == Cst.BIN_REL_REFINES) {
					addRefinement(r.@id);
				}
			}
			
			onDataUpdateComplete();
		}
		
		/*abstract*/
		protected function getFeatureDisplayName(o: Object): String {
			return UNNAMED;
		}
		
		/*abstract*/
		protected function isPartOfTree(o: Object): Boolean {
			return true;
		}
		
		/*abstract*/
		protected function onDataUpdateComplete(): void {
		}
		
		/*abstract*/
		protected function onDataUpdateStart(): void {
			
		}
		
		// ------------------------------------------------
		//         Event Handlers
		// ------------------------------------------------
		public function handleInferVoteOnFeature(op: Object): void {
			for each (var o: Object in op[Cst.FIELD_RSP_INFER_VOTES]) {
				// Only "YES" votes can be propagated to a feature, so we don't consider deletion here.
				checkAndAddFeature(String(o));
				updateFeatureSupportRate(String(o));
			}
		}
		
		public function handleInferVoteOnRelation(op: Object): void {
			for each (var id: Object in op[Cst.FIELD_RSP_INFER_VOTES]) {
				var removal: Boolean = false;
				var info: Object = null;
				var rel: XML = null;
				
				// See if the relationship has already been removed from the feature model
				if (op[FeatureModel.INFERRED_REMOVAL_ELEMENTS] != null) {
					for each (var o:Object in op[FeatureModel.INFERRED_REMOVAL_ELEMENTS]) {
						if (o.id == String(id)) {
							removal = true;
							info = o;
							break;
						}
					}
				}
				
				// See if the relationship is not removed from the feature model, but removed 
				// from this tree. (e.g. if I vote NO to a refinement and this is a working tree.)
				if (!removal) {
					var rs: XMLList = FeatureModel.instance().binaries.source.(@id==id);
					if (rs.length() > 0) {
						rel = rs[0];
						removal = !isPartOfTree(rel);
					}
				}
				
				if (removal) {
					// remove the refinement
					if (info != null && info.type == Cst.BIN_REL_REFINES) {
						removeRefinement(info.id, info.left, info.right);
					} else if (rel != null && rel.@type == Cst.BIN_REL_REFINES) {
						removeRefinement(rel.@id);
					}
				}
			}
		}
		
		public function handleAddAttribute(op: Object): void {
			// Do nothing, the tree doesn't show customized attributes
		}
		public function handleAddEnumAttribute(op: Object): void {
			// Do nothing
		}
		public function handleAddNumericAttribute(op: Object): void {
			//Do nothing
		}
		
		public function handleVoteAddValue(op: Object): void {
			// Only handles "Feature Name" attribute
			if (op["featureId"] == null || op["attr"] != Cst.ATTR_FEATURE_NAME) {
				return;
			}
			
			// recalculate the displayed name (primary name)
			var fs: XMLList = FeatureModel.instance().features.source.(@id==op["featureId"]);
			if (fs.length() <= 0) {
				return;
			}
			var name: String = this.getFeatureDisplayName(fs[0]);
			for each (var f: Object in this.getFeatureById(op["featureId"])) {
				f.@name = name;
			}
		}
		
		public function handleVoteAddFeature(op:Object): void {
			// if create, add the feature to the root
			if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
				var f: XML = this.createFeature(op["featureId"]);
				if (f != null) {
					this.root.appendChild(f);
				}
				return;
			}
			
			// Handle deletion. (Not part of model or not part of tree.)
			if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
				removeFeatureById(op["featureId"]);
				return;
			}
			var fs: XMLList = FeatureModel.instance().features.source.(@id==op["featureId"]);
			if (fs.length() > 0 && !isPartOfTree(fs[0])) {
				removeFeatureById(fs[0].@id);
				return;
			}
			
			// Handle voting (update support rate)
			if (ModelUtil.isTrue(op[Cst.FIELD_RSP_VOTE])) {
				checkAndAddFeature(op["featureId"]);
			}
			updateFeatureSupportRate(op["featureId"]);
		}
		
		public function handleVoteAddBinRel(op:Object): void {
			// The tree only deals with refinement relationships.
			if (op["type"] == Cst.BIN_REL_REFINES) {
				
				// Handle creation
				if (op[FeatureModel.IS_NEW_ELEMENT] == true || op[FeatureModel.FROM_OPPONENT_TO_SUPPORTER] == true) {
					this.addRefinement(op["relationshipId"]);
					return;
				}
				
				// Handle deletion
				var left: String = String(op["leftFeatureId"]);
				var right: String = String(op["rightFeatureId"]);
				if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
					removeRefinement(op["relationshipId"], left, right);
					return;
				}
				
				var rs: XMLList = FeatureModel.instance().binaries.source.(@id==op["relationshipId"]);
				if (rs.length() > 0 && !isPartOfTree(rs[0])) {
					removeRefinement(op["relationshipId"]);
					return;
				}
				
			}
		}
		
		protected function onPeopleLogout(evt: LogoutEvent): void {
			if (evt.user != UserList.instance().myId) {  // when other people logged out...
				var name: String = UserList.instance().getNameById(evt.user);
				removePersonLocation(name);
			}
		}
		
		protected function onPeopleExitModel(evt: PageSwitchEvent): void {
			if (evt.user != UserList.instance().myId && evt.model == ModelCollection.instance().currentModelId) {
				var name: String = UserList.instance().getNameById(evt.user);
				removePersonLocation(name);
			}
		}
		
		protected function onOtherPeopleSelect(evt: FeatureSelectEvent): void {
			var uName: String = UserList.instance().getNameById(evt.user);
			removePersonLocation(uName);
			addPersonLocation(String(evt.id), uName);			
		}
		
		public function getFeatureNameById(id: String): String {
			var fs: XMLList = this.getFeatureById(id);
			if (fs.length() > 0) {
				return fs[0].@name;
			}
			return null;
		}
		
		protected function onLocalModelUpdate(evt: ModelUpdateEvent): void {
			this.refresh();
		}
		
		protected function containsFeature(id: String): Boolean {
			return this.getFeatureById(id).length() > 0;
		}
		
		
		public function stats(): String {
			//1. The number of features
			var numFeature: int = 0;
			var features: Dictionary = new Dictionary();
			for each (var o: Object in this.root..feature) {
				if (features[o.@id] != undefined) {
					continue;
				}
				features[o.@id] = o;
				numFeature ++;
			}
			
			//2. The number of my creation
			var numMy: int = 0;
			for each (var o2: Object in features) {
				if (o2.@creator == String(UserList.instance().myId)) {
					numMy++;
				}
			}
			return "Total: " + numFeature + " features; My creation: " + numMy;
		}
	}
}