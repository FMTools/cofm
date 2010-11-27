package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.collections.XMLListCollection;
	
	public class ClassificationTreeData implements IOperationListener {
		
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
		
		public static const UNNAMED: String = "<unnamed>";
		public static const KIND_CLASS: String = "CLASS";
		public static const KIND_OBJECT: String = "OBJECT";
		
		private static var _instance: ClassificationTreeData = new ClassificationTreeData();
		
		public static function instance(): ClassificationTreeData {
			return _instance;
		}
		
		public function ClassificationTreeData() {
			this.xml = new XMLListCollection(new XMLList(<node/>));
			
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.LOCAL_MODEL_COMPLETE, onLocalModelUpdate);
			
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
			if (fs.length() <= 0) {
				return null;
			}
			var name: String = getEntityDisplayName(fs[0]);
			return <node id={id} 
			kind={ClassificationTreeData.KIND_OBJECT}
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
				var ns: XMLList = this.getNodeById(ClassificationTreeData.KIND_CLASS, cs.@id);
				if (ns.length() > 0) {
					base = ns[0];
					break;
				}
				var thisNode: XML = <node id={cs.@id}
				kind={ClassificationTreeData.KIND_CLASS}
				name={cs.@name}
				superId={cs.@superId} />;
				nodeStack.push(thisNode);
				var superId: Number = new Number(cs.@superId);
				if (!isNaN(superId)){
					var idstr: String = superId.toString();
					var supers: XMLList = Model.instance().entypes.source.(@id==idstr);
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
			var targets: XMLList = getNodeById(ClassificationTreeData.KIND_OBJECT, id);
			if (targets.length() > 0) {
				ModelUtil.clearXMLList(targets);
			}
		}
		
		
		private function addToRootOfClass(node: XML): void {
			// Actually, move node to the root of its Class-Node
			var classNode: XMLList = this.getNodeById(ClassificationTreeData.KIND_CLASS, node.@typeId);
			if (classNode.length() > 0) {
				XML(classNode[0]).appendChild(node);
			}
		}
		
		protected function updateEntitySupportRate(id: String): void {
			var sr: Number = Model.instance().getEntitySupportRate(id);
			for each (var f: Object in this.getNodeById(ClassificationTreeData.KIND_OBJECT, id)) {
				f.@support = sr;
			}
		}
		
		/**
		 * Clear and re-build the tree
		 */
		protected function refresh(): void {
			beforeDataUpdating();
			
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
			
			afterDataUpdated();
		}
		
		protected function getEntityDisplayName(feature: Object): String {
			var allNames: XMLList = Model.instance().getValuesByAttrName(XML(feature), Cst.ATTR_FEATURE_NAME);
			if (allNames.length() <= 0) {
				return UNNAMED;
			}
			// Build an array to sort.
			var ns: Array = [];
			for each (var n: Object in allNames) {
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					yes.push(XML(u).text().toString());
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(XML(u1).text().toString());
				}
				ns.push({
					val: XML(n.str).text().toString(),
					v1: yes,
					v0: no
				});
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance().myId);
			return ns[0].val;
		}
		
		protected function afterDataUpdated(): void {
		}
		
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
			// Do nothing
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
			for each (var f: Object in this.getNodeById(ClassificationTreeData.KIND_OBJECT, op["entityId"])) {
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
			
			// Handle deletion. 
			if (op[Model.SHOULD_DELETE_ELEMENT] == true) {
				removeEntityById(op["entityId"]);
				return;
			}

			// Handle voting (update support rate)
			if (ModelUtil.isTrue(op[Cst.FIELD_RSP_VOTE])) {
				checkAndAddEntity(op["entityId"]);
			}
			updateEntitySupportRate(op["entityId"]);
		}
		
		public function handleVoteAddBinRel(op:Object): void {
			// Do nothing
		}
		
		public function getEntityNameById(id: String): String {
			var fs: XMLList = this.getNodeById(ClassificationTreeData.KIND_OBJECT, id);
			if (fs.length() > 0) {
				return fs[0].@name;
			}
			return null;
		}
		
		protected function onLocalModelUpdate(evt: ModelUpdateEvent): void {
			this.refresh();
		}
		
		protected function containsEntity(id: String): Boolean {
			return this.getNodeById(ClassificationTreeData.KIND_OBJECT, id).length() > 0;
		}
		
	}
}