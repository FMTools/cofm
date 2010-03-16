package collab.fm.client.data {
	import collab.fm.client.event.*;

	import mx.collections.ArrayCollection;

	public class FeatureNameList extends OperationListener {
		private static var _instance: FeatureNameList = new FeatureNameList();

		private var _data: ArrayCollection;

		public static function get instance(): FeatureNameList {
			return _instance;
		}

		public function FeatureNameList() {
			super();
			_data = new ArrayCollection();
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.LOCAL_MODEL_COMPLETE, onLocalModelUpdate);
		}

		override protected function handleAddDescription(op:Object): void {

		}

		override protected function handleAddName(op:Object): void {

		}

		override protected function handleCreateFeature(op:Object): void {
			// check and add the name
		}

		override protected function handleCreateRelationship(op:Object): void {

		}

		override protected function handleSetOpt(op:Object): void {

		}

		private function onLocalModelUpdate(evt: ModelUpdateEvent): void {
			data.removeAll();
			for each (var o: Object in FeatureModel.instance.features.source) {
				var f: XML = XML(o);
				// Get all names of the feature f.
				for each (var obj: Object in f..name) {
					var info: Object = new Object();
					info.id = f.@id;
					info.name = String(obj);
					data.addItem(info);
				}
			}
		}

		[Bindable]
		public function get data(): ArrayCollection {
			return _data
		}

		public function set data(d: ArrayCollection): void {
			_data = d;
		}

	}
}