package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.collections.ArrayCollection;
	
	public class FeatureNameList implements IOperationListener
	{
		private static var _instance: FeatureNameList = new FeatureNameList();
		
		public static const UNUSED_NAME_ID: int = -1;
		public static const ON_CREATING_NAME_ID: int = -2;
		
		[Bindable] public var names: ArrayCollection;
		
		private var nameIdMap: Dictionary;
		
		public static function instance(): FeatureNameList {
			return _instance;
		}
		
		public function FeatureNameList() {
			names = new ArrayCollection();
			nameIdMap = new Dictionary();
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.LOCAL_MODEL_UPDATED, onLocalModelUpdate);
			Model.instance().registerSubView(this);
		}
		
		public function contains(name: String): Boolean {
			return nameIdMap[name] != null;
		}
		
		public function getIdByName(name: String): int {
			var id: Object = nameIdMap[name];
			if (id != null) {
				return int(id);
			}
			return UNUSED_NAME_ID;
		}
		
		private function addName(name: String, id: int): void {
			if (nameIdMap[name] == null) {
				names.addItem(name);
			}
			nameIdMap[name] = id;
		}
		
		private function removeByName(name: String): void {
			names.removeItemAt(names.getItemIndex(name));
			delete nameIdMap[name];
		}
		
		private function removeById(id: int): void {
			for (var key: Object in nameIdMap) {
				if (int(nameIdMap[key]) == id) {
					removeByName(String(key));
					return;
				}
			}
		}
		
		public function handleEditAddEntityType(op: Object): void {
			// TODO
		}
		
		public function handleEditAddBinRelType(op: Object): void {
			// TODO
		}
		
		public function handleInferVoteOnEntity(op: Object): void {
			// Do nothing
		}
		
		public function handleInferVoteOnRelation(op: Object): void {
			// Do nothing
		}
		
		public function handleEditAddAttributeDef(op: Object): void {
			// Do nothing
		}
		
		public function handleEditAddEnumAttributeDef(op: Object): void {
			// Do nothing
		}
		
		public function handleEditAddNumericAttributeDef(op: Object): void {
			// Do nothing
		}
		
		public function handleVoteAddValue(op: Object): void {
			// Only handle add "FeatureName" to a feature
			if (op["featureId"] == null || op["attr"] != Cst.ATTR_FEATURE_NAME) {
				return;
			}
			
			var n: String = String(op["val"]);
			if (op[Model.IS_NEW_ELEMENT] == true) {
				addName(n, int(op["featureId"]));
			}
			
			if (op[Model.SHOULD_DELETE_ELEMENT] == true) {
				removeByName(n);
			}
		}
		
		public function handleVoteAddEntity(op:Object): void {
			// if new feature
			if (op[Model.IS_NEW_ELEMENT] == true) {
				addName(String(op["featureName"]), int(op["featureId"]));
			}
			
			if (op[Model.SHOULD_DELETE_ELEMENT] == true) {
				removeById(int(op["featureId"]));
			}
			//			if (op[FeatureModel.VOTE_NO_TO_FEATURE] == true) {
			//				reset();
			//			}
		}
		
		public function handleVoteAddBinRel(op:Object): void {
			// do nothing
		}
		
		private function onLocalModelUpdate(evt: ModelUpdateEvent): void {
			reset();
			//	Console.info("FeatureNameList - Model refreshed. Reset name list.");
		}
		
		private function reset(): void {
			names.source = [];
			nameIdMap = new Dictionary();
			for each (var o: Object in Model.instance().entities.source) {
				var f: XML = XML(o);
				//trace (f.toXMLString());
				// Get all names of the feature f.
				for each (var obj: Object in Model.instance().getValuesByAttrName(f, Cst.ATTR_FEATURE_NAME)) {
					addName(XML(obj.str).text().toString(), int(f.@id));
				}
			}
		}
	}
}