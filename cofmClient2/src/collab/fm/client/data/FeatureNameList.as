package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	import mx.collections.ArrayCollection;
	import mx.collections.IViewCursor;

	public class FeatureNameList implements IOperationListener {
		private static var _instance: FeatureNameList = new FeatureNameList();

		private var _data: ArrayCollection;

		public static function get instance(): FeatureNameList {
			return _instance;
		}

		public function FeatureNameList() {
			_data = new ArrayCollection();
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.LOCAL_MODEL_COMPLETE, onLocalModelUpdate);
			FeatureModel.instance.registerSubView(this);
			trace("FeatureNameList inited.");
		}

		public function contains(name: String): Boolean {
			for each (var obj: Object in data.source) {
				if (obj.name == name) {
					return true;
				}
			}
			return false;
		}
		
		public function getIdByName(name: String): int {
			var o: Object = getByName(name);
			if (o != null) {
				return int(o.id);
			}
			return -1;
		}
		
		public function getByName(name: String): Object {
			for each (var obj: Object in data.source) {
				if (obj.name == name) {
					return obj;
				}
			}	
			return null;
		}
		
		public function handleInferVoteOnFeature(op: Object): void {
			// Do nothing
		}

		public function handleInferVoteOnRelation(op: Object): void {
			// Do nothing
		}
		
		public function handleAddAttribute(op: Object): void {
			// Do nothing
		}
		
		public function handleAddEnumAttribute(op: Object): void {
			// Do nothing
		}
		
		public function handleAddNumericAttribute(op: Object): void {
			// Do nothing
		}
		
		public function handleVoteAddValue(op: Object): void {
			// Only handle add "FeatureName" to a feature
			if (op["featureId"] == null || op["attr"] != Cst.ATTR_FEATURE_NAME) {
				return;
			}
			
			if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
				data.addItem({id: op["featureId"], name: op["val"]});
				Console.info("FeatureNameList - add item (" + op["featureId"] + ", " + op["val"] + ")");
			}
			
			if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
				for (var cursor: IViewCursor = data.createCursor(); !cursor.afterLast; ) {
					if (cursor.current.id == op["featureId"] &&
						cursor.current.name == op["val"]) {
						cursor.remove();
						break;
					} else {
						cursor.moveNext();
					}
				}
			}
		}
		
		public function handleVoteAddFeature(op:Object): void {
			// if new feature
			if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
				data.addItem({id: op["featureId"], name: op["featureName"]});
				Console.info("FeatureNameList - add item (" + op["featureId"] + ", " + op["featureName"] + ")");
			}
			if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
				for (var cursor: IViewCursor = data.createCursor(); !cursor.afterLast; ) {
					if (cursor.current.id == op["featureId"]) {
						cursor.remove();
						break;
					} else {
						cursor.moveNext();
					}
				}
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
			data.source = [];
			for each (var o: Object in FeatureModel.instance.features.source) {
				var f: XML = XML(o);
				//trace (f.toXMLString());
				// Get all names of the feature f.
				for each (var obj: Object in FeatureModel.instance.getValuesOfAttr(f, Cst.ATTR_FEATURE_NAME)) {
					//trace ("Name: " + XML(obj).toXMLString());
					var info: Object = new Object();
					info.id = String(f.@id);
					info.name = XML(obj.str).text().toString();
					data.addItem(info);
				//	trace ("*Added: " + info.id + ", " + info.name);
				}
			}
		}

		[Bindable]
		public function get data(): ArrayCollection {
			return _data;
		}

		public function set data(d: ArrayCollection): void {
			_data = d;
		}

	}
}