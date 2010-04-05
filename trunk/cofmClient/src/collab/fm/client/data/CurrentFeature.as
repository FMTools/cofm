package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import mx.collections.ArrayCollection;

	public class CurrentFeature implements IOperationListener {
		private static var _instance: CurrentFeature = new CurrentFeature();

		private var _feature: XML;

		public var id: int;

		[Bindable]
		public var names: ArrayCollection;

		public static function get instance(): CurrentFeature {
			return _instance;
		}

		public function CurrentFeature() {
			names = new ArrayCollection();
			FeatureModel.instance.registerSubView(this);

			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.DB_CLICK_ON_TREE, onCurrentFeatureSelected);
		}

		private function onCurrentFeatureSelected(evt: FeatureSelectEvent): void {
			// Set the feature id
			id = evt.id;
			_feature = XML(FeatureModel.instance.features.source.(@id==String(evt.id))[0]);

			updateVotes(); // votes to this feature
			updateNames();
			updateDescriptions();
			updateOptionality();
			updateRefinements();
			updateRequirings();
			updateExcludings();
		}

		private function updateVotes(): void {

		}

		private function updateNames(): void {
			names.removeAll();
			// Construct the "names" array for Feature_Name_DataGrid.
			// Columns: name, supporters (with percentage), opponents.
			for each (var _name: Object in _feature.names.name) {
				names.addItem({
						"name": _name.@val,
						"supporters": XMLList(_name.yes.user).length(),
						"opponents": XMLList(_name.no.user).length()
					});
			}
		}

		private function updateDescriptions(): void {

		}

		private function updateOptionality(): void {

		}

		private function updateRefinements(): void {

		}

		private function updateRequirings(): void {

		}

		private function updateExcludings(): void {

		}

		public function handleAddDescription(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateDescriptions();
			}
		}

		public function handleAddName(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateNames();
			}
		}

		public function handleCreateFeature(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateVotes();
			}
		}

		public function handleCreateBinaryRelationship(op:Object): void {
			if (op["leftFeatureId"] == String(id) || op["rightFeatureId"] == String(id)) {
				switch (op["type"]) {
					case Cst.BIN_REL_REFINES:
						this.updateRefinements();
						break;
					case Cst.BIN_REL_REQUIRES:
						this.updateRequirings();
						break;
					case Cst.BIN_REL_EXCLUDES:
						this.updateExcludings();
						break;
				}
			}
		}

		public function handleSetOpt(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateOptionality();
			}
		}

		public function handleFeatureVotePropagation(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateVotes();
			}
		}

		public function handleRelationshipVotePropagation(op:Object): void {
			// Check for removed relationships.
			if (op[FeatureModel.SHOULD_DELETE_ELEMENT] != null) {
				this.updateExcludings();
				this.updateRefinements();
				this.updateRequirings();
			}
		}
	}
}