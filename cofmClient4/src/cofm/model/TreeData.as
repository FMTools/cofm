package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.collections.XMLListCollection;
	
	public class TreeData implements IOperationListener {
		
		/** The XML of tree
		 *  <node>
		 *      <node ... >
		 * </node>
		 *  Tree nodes:
		 *      <node kind="Class|Object" id= creator= time= support= 
		 *                  parents={number of parents} name= people={people who are editing on me}>
		 *          <node .../>
		 *      </node>
		 */
		[Bindable] public var xml: XMLListCollection;
		
		protected var refinements: Dictionary = new Dictionary();
		
		public static const UNNAMED: String = "<unnamed>";
		public static const KIND_CLASS: String = "CLASS";
		public static const KIND_OBJECT: String = "OBJECT";
		
		public function TreeData() {
			this.xml = new XMLListCollection(new XMLList(<node/>));
			
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.OTHER_PEOPLE_SELECT_ON_TREE, onOtherPeopleSelect);
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.LOCAL_MODEL_COMPLETE, onLocalModelUpdate);
			ClientEvtDispatcher.instance().addEventListener(
				LogoutEvent.LOGGED_OUT, onPeopleLogout);
			ClientEvtDispatcher.instance().addEventListener(
				PageSwitchEvent.OTHERS_EXIT_WORK_PAGE, onPeopleExitModel);
			
			Model.instance().registerSubView(this);
		}
		
		//--------------------------------------------------
		//   Tree Manipulation
		//--------------------------------------------------
		protected function get root(): XML {
			return XML(this.xml.getItemAt(0));
		}
		
		protected function getNodeById(kind: String, id: String): XMLList {
			return this.root..node.(@kind==kind && @id==id);		
		}
		
		protected function createEntity(id: String): XML {
			var fs: XMLList = Model.instance().entities.source.(@id==id);
			if (fs.length() <= 0 || !isPartOfTree(fs[0])) {
				return null;
			}
			var name: String = getEntityDisplayName(fs[0]);
			return <node id={id} 
				kind={TreeData.KIND_OBJECT}
				typeId={fs[0].@typeId}
				creator={fs[0].@creator}
				time={fs[0].@mtime}
				support={Model.instance().getSupportRate(fs[0])}
				parents="0"
				name={name}
				people="" />; 
		}
		
		protected function createClassHierarchy(entype: XML): void {
			// make sure the super class nodes are created first
			var nodeStack: Array = new Array();
			var cs: XML = entype;
			var base: XML = null;
			while (cs != null) {
				var ns: XMLList = this.getNodeById(TreeData.KIND_CLASS, cs.@id);
				if (ns.length() > 0) {
					base = ns[0];
					break;
				}
				var thisNode: XML = <node id={cs.@id}
						kind={TreeData.KIND_CLASS}
						name={cs.@name}
						superId={cs.@superId} />;
				nodeStack.push(thisNode);
				var superId: Number = new Number(cs.@superId);
				if (!isNaN(superId) && superId > 0 ){
					var supers: XMLList = Model.instance().entypes.source.(@id==superId.toString());
					if (supers.length() > 0) {
						cs = supers[0];
					} else {
						cs = null;
					}
				} else {
					break;
				}
			}
			if (nodeStack.length <= 0) {
				return;
			}
			
			// Append the first node
			var first: XML = nodeStack.pop();
			if (base == null) {
				this.root.appendChild(first);
			} else {
				base.appendChild(first);
			}
			
			// Then append the others
			var cur: XML = first;
			while (nodeStack.length > 0) {
				cur.appendChild(nodeStack.pop());
				cur = cur.children()[0];
			}
		}
		
		protected function checkAndAddEntity(id: String): void {
			if (!this.containsEntity(id)) {
				var f: XML = this.createEntity(id);
				if (f != null) {
					addToRootOfClass(f);
				}
			}
		}
		
		protected function removeEntityById(id: String): void {
			var targets: XMLList = getNodeById(TreeData.KIND_OBJECT, id);
			// for all children of targets, we should remove refinement of them first.
			// After that, we remove the targets.
			if (targets.length() > 0) {
				var p: XML = targets[0];
				for each (var c: Object in p.children()) {
					removeRefinement(Model.instance().getRefinementId(p.@id, c.@id));
				}
				ModelUtil.clearXMLList(targets);
			}
		}
		
		protected function addRefinement(id: String): void {
			if (this.refinements[id] == true) { // If the refinement has already existed, return now.
				return;
			}
			var rs: XMLList = Model.instance().binaries.source.(@id==id);
			if (rs.length() <= 0 || 
				!Model.instance().isRefinement(XML(rs[0])) || 
				!isPartOfTree(rs[0])) {
				return;
			}
			var parent: String = rs[0].@sourceId;
			var child: String = rs[0].@targetId;
			var p: XMLList = this.getNodeById(TreeData.KIND_OBJECT, parent);
			var c: XMLList = this.getNodeById(TreeData.KIND_OBJECT, child);
			
			ensureNonEmptyList(p, parent);
			ensureNonEmptyList(c, child);
			
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
					if (XMLList(pa.node.(@id==ch.@id)).length() <= 0) {
						hasNewParent = true;
						XML(pa).appendChild(ch.copy());
					}
				}
				if (hasNewParent) {
					// Increase child.parents by 1.
					for each (var chd: Object in this.getNodeById(TreeData.KIND_OBJECT, child)) {
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
				var rs: XMLList = Model.instance().binaries.source.(@id==id);
				if (rs.length() <= 0 || !Model.instance().isRefinement(XML(rs[0]))) {
					return;
				}
				parent = rs[0].@sourceId;
				child = rs[0].@targetId;
			} else {
				parent = parentId;
				child = childId;
			}
			
			var p: XMLList = this.getNodeById(TreeData.KIND_OBJECT, parent);
			var c: XMLList = this.getNodeById(TreeData.KIND_OBJECT, child);
			
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
					ModelUtil.clearXMLList(XMLList(pa.node.(@id==ch.@id)));
				}
				if (ch.@parents == "1") {  // make ch a root feature
					addToRootOfClass(ch);
				}
				for each (var chd: Object in this.getNodeById(TreeData.KIND_OBJECT, child)) {
					chd.@parents = int(chd.@parents) - 1;
				}
				
				// Remove refinement from dictionary
				delete this.refinements[id];
			}
		}
		
		private function ensureNonEmptyList(list: XMLList, id: String): void {
			if (list.length() <= 0) {
				var node: XML = this.createEntity(id);
				if (node != null) {
					this.root.appendChild(node);
					list[0] = node;
				}
			}
		}
		
		private function addToRootOfClass(node: XML): void {
			// Actually, move node to the root of its Class-Node
			var classNode: XMLList = this.getNodeById(TreeData.KIND_CLASS, node.@typeId);
			if (classNode.length() > 0) {
				XML(classNode[0]).appendChild(node);
			}
		}
		
		protected function addPersonLocation(id: String, person: String): void {
			for each (var node: Object in getNodeById(TreeData.KIND_OBJECT, id)) {
				var people: String = node.@people;
				if (people != "") {
					people += ", ";   // append a person to other people.
				}
				people += person;
				node.@people = people;
			}
		}
		
		protected function removePersonLocation(person: String): void {
			for each (var node: Object in this.root..node.(@kind==TreeData.KIND_OBJECT)) {	
				var people: Array = String(node.@people).split(/,\s*/);
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
				node.@people = rslt;
			}
		}
		
		protected function updateEntitySupportRate(id: String): void {
			var sr: Number = Model.instance().getEntitySupportRate(id);
			for each (var f: Object in this.getNodeById(TreeData.KIND_OBJECT, id)) {
				f.@support = sr;
			}
		}
		
		/**
		 * Clear and re-build the tree
		 */
		protected function refresh(): void {
			beforeDataUpdating();
			
			this.refinements = new Dictionary();
			
			this.xml = new XMLListCollection(new XMLList(<node/>));
			
			// Create the Class Nodes
			for each (var c: Object in Model.instance().entypes.source) {
				this.createClassHierarchy(XML(c));
			}
			
			// Create the Object Nodes
			for each (var f: Object in Model.instance().entities.source) {
				var node: XML = this.createEntity(f.@id);
				if (node != null) {
					addToRootOfClass(node);
				}
			}	
			// Create Refinements between Objects
			for each (var r: Object in Model.instance().binaries.source) {
				if (r.@type == Cst.BIN_REL_REFINES) {
					addRefinement(r.@id);
				}
			}
			
			afterDataUpdated();
		}
		
		/*abstract*/
		protected function getEntityDisplayName(o: Object): String {
			return UNNAMED;
		}
		
		/*abstract*/
		protected function isPartOfTree(o: Object): Boolean {
			return true;
		}
		
		/*abstract*/
		protected function afterDataUpdated(): void {
		}
		
		/*abstract*/
		protected function beforeDataUpdating(): void {
			
		}
		
		// ------------------------------------------------
		//         Event Handlers
		// ------------------------------------------------
		public function handleEditAddEntityType(op: Object): void {
			var _entypes: XMLList = Model.instance().entypes.source.(@id==op["typeId"]);
			if (_entypes.length() > 0) {
				this.createClassHierarchy(XML(_entypes[0]));
			}
		}
		
		public function handleEditAddBinRelType(op: Object): void {
			// Do nothing now.
		}
		
		public function handleInferVoteOnEntity(op: Object): void {
			for each (var o: Object in op[Cst.FIELD_RSP_INFER_VOTES]) {
				// Only "YES" votes can be propagated to a feature, so we don't consider deletion here.
				checkAndAddEntity(String(o));
				updateEntitySupportRate(String(o));
			}
		}
		
		public function handleInferVoteOnRelation(op: Object): void {
			for each (var id: Object in op[Cst.FIELD_RSP_INFER_VOTES]) {
				var removal: Boolean = false;
				var info: Object = null;
				var rel: XML = null;
				
				// See if the relationship has already been removed from the feature model
				if (op[Model.INFERRED_REMOVAL_ELEMENTS] != null) {
					for each (var o:Object in op[Model.INFERRED_REMOVAL_ELEMENTS]) {
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
					var rs: XMLList = Model.instance().binaries.source.(@id==id);
					if (rs.length() > 0) {
						rel = rs[0];
						removal = !isPartOfTree(rel);
					}
				}
				
				if (removal) {
					// remove the refinement
					if (info != null && Model.instance().isRefinement(XML(info))) {
						removeRefinement(info.@id, info.@sourceId, info.@targetId);
					} else if (rel != null && Model.instance().isRefinement(rel)) {
						removeRefinement(rel.@id, rel.@sourceId, rel.@targetId);
					}
				}
			}
		}
		
		public function handleEditAddAttributeDef(op: Object): void {
			// Do nothing, the tree doesn't show attributes
		}
		public function handleEditAddEnumAttributeDef(op: Object): void {
			// Do nothing
		}
		public function handleEditAddNumericAttributeDef(op: Object): void {
			//Do nothing
		}
		
		public function handleVoteAddValue(op: Object): void {
			
			if (op["entityId"] == null) {
				return;
			}
			var fs: XMLList = Model.instance().entities.source.(@id==op["entityId"]);
			if (fs.length() <= 0) {
				return;
			}
			// Only handles "Feature Name" attribute
			if (Model.instance().getAttrNameById(fs[0], op["attrId"]) != Cst.ATTR_FEATURE_NAME) {
				return;
			}
			
			// recalculate the displayed name (primary name)
			var name: String = this.getEntityDisplayName(fs[0]);
			for each (var f: Object in this.getNodeById(TreeData.KIND_OBJECT, op["entityId"])) {
				f.@name = name;
			}
		}
		
		public function handleVoteAddEntity(op:Object): void {
			// if create, add the feature to the root
			if (op[Model.IS_NEW_ELEMENT] == true) {
				var f: XML = this.createEntity(op["entityId"]);
				if (f != null) {
					this.addToRootOfClass(f);
				}
				return;
			}
			
			// Handle deletion. (Not part of model or not part of tree.)
			if (op[Model.SHOULD_DELETE_ELEMENT] == true) {
				removeEntityById(op["entityId"]);
				return;
			}
			var fs: XMLList = Model.instance().entities.source.(@id==op["entityId"]);
			if (fs.length() > 0 && !isPartOfTree(fs[0])) {
				removeEntityById(fs[0].@id);
				return;
			}
			
			// Handle voting (update support rate)
			if (ModelUtil.isTrue(op[Cst.FIELD_RSP_VOTE])) {
				checkAndAddEntity(op["entityId"]);
			}
			updateEntitySupportRate(op["entityId"]);
		}
		
		public function handleVoteAddBinRel(op:Object): void {
			// The tree only deals with refinement relationships.
			if (op[Model.IS_A_REFINEMENT] == true) {
				
				// Handle creation
				if (op[Model.IS_NEW_ELEMENT] == true || op[Model.FROM_OPPONENT_TO_SUPPORTER] == true) {
					this.addRefinement(op["relationId"]);
					return;
				}
				
				// Handle deletion
				var left: String = String(op["sourceId"]);
				var right: String = String(op["targetId"]);
				if (op[Model.SHOULD_DELETE_ELEMENT] == true) {
					removeRefinement(op["relationId"], left, right);
					return;
				}
				
				var rs: XMLList = Model.instance().binaries.source.(@id==op["relationId"]);
				if (rs.length() > 0 && !isPartOfTree(rs[0])) {
					removeRefinement(op["relationId"]);
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
		
		public function getEntityNameById(id: String): String {
			var fs: XMLList = this.getNodeById(TreeData.KIND_OBJECT, id);
			if (fs.length() > 0) {
				return fs[0].@name;
			}
			return null;
		}
		
		protected function onLocalModelUpdate(evt: ModelUpdateEvent): void {
			this.refresh();
		}
		
		protected function containsEntity(id: String): Boolean {
			return this.getNodeById(TreeData.KIND_OBJECT, id).length() > 0;
		}
		
		
		public function stats(): String {
			//1. The number of features
			var numFeature: int = 0;
			var features: Dictionary = new Dictionary();
			for each (var o: Object in this.root..node) {
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