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
			for each (var obj: Object in data.source) {
				if (obj.name == name) {
					return int(obj.id);
				}
			}	
			return -1;
		}
		
		public function handleFeatureVotePropagation(op: Object): void {

		}

		public function handleRelationshipVotePropagation(op: Object): void {

		}

		public function handleAddDescription(op:Object): void {

		}

		public function handleAddName(op:Object): void {
			if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
				data.addItem({id: op["featureId"], name: op["value"]});
				Console.info("FeatureNameList - add item (" + op["featureId"] + ", " + op["value"] + ")");
			}
			if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
				for (var cursor: IViewCursor = data.createCursor(); !cursor.afterLast; ) {
					if (cursor.current.id == op["featureId"] &&
						cursor.current.name == op["value"]) {
						cursor.remove();
						break;
					} else {
						cursor.moveNext();
					}
				}
			}
		}

		public function handleCreateFeature(op:Object): void {
			// if new feature
			if (op[FeatureModel.IS_NEW_ELEMENT] == true) {
				data.addItem({id: op["featureId"], name: op["value"]});
				Console.info("FeatureNameList - add item (" + op["featureId"] + ", " + op["value"] + ")");
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
			if (op[FeatureModel.VOTE_NO_TO_FEATURE] == true) {
				trace("---- FeatureNmeList - vote no to feature");
				reset();
				for each (var o: Object in data.source) {
					trace(o.id + ", " + o.name);
				}
			}
		}

		public function handleCreateBinaryRelationship(op:Object): void {
			// do nothing
		}

		public function handleSetOpt(op:Object): void {

		}

		private function onLocalModelUpdate(evt: ModelUpdateEvent): void {
			reset();
		//	Console.info("FeatureNameList - Model refreshed. Reset name list.");
		}

		private function reset(): void {
			data.source = [];
//			trace("---- reset()  data.removeAll() ---");
//			for each (var ob: Object in data.source) {
//					trace(ob.id + ", " + ob.name);
//			}
			for each (var o: Object in FeatureModel.instance.features.source) {
				var f: XML = XML(o);
				//trace (f.toXMLString());
				// Get all names of the feature f.
				for each (var obj: Object in f..name) {
					//trace ("Name: " + XML(obj).toXMLString());
					var info: Object = new Object();
					info.id = String(f.@id);
					info.name = String(obj.@val);
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