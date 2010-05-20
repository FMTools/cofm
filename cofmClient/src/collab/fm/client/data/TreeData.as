package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.collections.XMLListCollection;

	public /*abstract*/class TreeData {

		/** The XML of global trees:
		 *    <feature id=X name=X controversy=0..to..1>
		 * 		<feature/>
		 * 		<feature/>
		 * 	  </feature>
		 */
		protected var _data: XMLListCollection;

		public function TreeData() {
			_data = new XMLListCollection();
			
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.OTHER_PEOPLE_SELECT_ON_TREE, onOtherPeopleSelect);
		}
		
		protected function onOtherPeopleSelect(evt: FeatureSelectEvent): void {
			// 1. Remove this person's previous location.
			var uName: String = UserList.instance.getNameById(evt.user);
			var p: XMLList = this.xml.source.(@person==uName);
			var p2: XMLList = this.xml.source..feature.(@person==uName);
			for each (var o: Object in p) {
				o.@person = "";
			}
			for each (var o2: Object in p2) {
				o2.@person = "";
			}
			var nodes: XMLList = ModelUtil.getRootFeatureById(this.xml.source, String(evt.id));
			var nodes2: XMLList = ModelUtil.getNonRootFeatureById(this.xml.source, String(evt.id));
			for each (var n: Object in nodes) {
				updatePeopleLocation(n, uName);
			}
			for each (var n2: Object in nodes2) {
				updatePeopleLocation(n2, uName);
			}
		}
		
		protected function updatePeopleLocation(node: *, person: String): void {
			if (node == undefined || node == null) {
				return;
			}
			node.@person = person;
			updatePeopleLocation(XML(node).parent(), person);
		}
		
		public function getNameById(id: String): String {
			var fs: XMLList = ModelUtil.getRootFeatureById(this.xml.source, id);
			if (fs.length() <= 0) {
				fs = ModelUtil.getNonRootFeatureById(this.xml.source, id);
			}
			if (fs.length() > 0) {
				return fs[0].@name;
			}
			return null;
		}

		protected function refreshData(evt: ModelUpdateEvent): void {
			onDataUpdateStart();
			
			var refines: Dictionary = new Dictionary();
			// refines: key = id, value = parent & children
			//   parent == [] (empty array) indicates a root feature.
			//   parent == null indicates an non-positioned feature.
			for each (var item: Object in FeatureModel.instance.binaries.source) {
				var r: XML = XML(item);
				if (String(r.@type).toLowerCase() != "refines") {
					continue;
				}
				addRefinement(refines, r);
			}
			var topNodes: Array = [];
			for each (var node: Object in FeatureModel.instance.features.source) {
				var curId: String = XML(node).@id;
				if (isTopNode(curId, refines)) {
					topNodes.push(curId);
				}
			}

			var root: XML = <root/>;
			for each (var id: Object in topNodes) {
				var nd: XML = createTreeNode(String(id), refines);
				if (nd != null) {
					root.appendChild(nd);
				}
			}

			xml.source = root.feature;
			onDataUpdateComplete();
		}

		protected function createTreeNode(id: String, refines: Dictionary): XML {
			var node: XML = createSpecificTreeNode(id, refines);
			if (refines[id] == null) {
				return node;
			}
			for each (var item: Object in refines[id].child) {
				var c: XML = createTreeNode(String(item), refines);
				if (c != null) {
					node.appendChild(c);
				}
			}
			return node;
		}

		/*abstract, return null if this node should not present in the tree.*/
		protected function createSpecificTreeNode(id: String, refines: Dictionary): XML {
			return <feature />;
		}

		/*abstract*/
		protected function addRefinement(refines: Dictionary, relation: XML): void {
		}

		/*abstract*/
		protected function isTopNode(id: String, refines: Dictionary): Boolean {
			return true;
		}

		/*abstract*/
		protected function getPrimaryName(feature: XML): String {
			return null;
		}

		/*abstract*/
		protected function onDataUpdateComplete(): void {
		}
		
		/*abstract*/
		protected function onDataUpdateStart(): void {
			
		}

		[Bindable]
		public function get xml(): XMLListCollection {
			return _data;
		}

		public function set xml(x: XMLListCollection): void {
			_data = x;
		}

	}
}