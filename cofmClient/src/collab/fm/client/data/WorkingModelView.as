package collab.fm.client.data {
	import collab.fm.client.util.ModelUtil;
	
	import mx.collections.XMLListCollection;

	// How to generate a working tree from model:
	//  working tree = model_tree - {elements which I voted NO}
	//  model_tree = feature_primary_name + children
	//  feature_primary_name = name I voted YES, or the most supported name if I haven't voted.

	public class WorkingModelView extends AbstractDataView {
		
		private var _data: XMLListCollection;
		
		private var model: Model = Model.instance;

		private function createFeatureXmlNode(feature: Object): XML {
			// Decide whether the feature should be ignored
			var opponents: Array = feature.uNo;
			if (opponents.indexOf(model.myId) >= 0) {
				return null;
			}

			// Generate the XML for this feature: 
			//    <feature id=X name=primary_name />
			var _id: int = feature.id;
			var _name: String = null;

			// Calculate the primary name, the names are already sorted by the supporting rate
			var names: Array = feature.name;
			for each (var n: Object in names) {
				// Try to use current name as primary name
				if (_name == null) {
					_name = n.val;
				}
				// Check if I'm a opponent of the name
				var nameOpponents: Array = n.uNo;
				if (nameOpponents.indexOf(model.myId) >= 0) {
					_name = null;
				}
				// Check if I'm a supporter of the name
				var nameSupporters: Array = n.uYes;
				if (nameSupporters.indexOf(model.myId) >= 0) {
					_name = n.val;
					break;
				}
			}

			if (_name == null) {
				// I'm opponent of all names, use the most popular name
				_name = names[0].val;
			}

			var result: XML = <feature id={_id} name={_name} />;

			return result;
		}

		private function updateEntireView(): void {
			var root: XML = <root/>;

			// Create the tree recursively.
			function nodeToXml(node: Object): XML {
				var me: XML = createFeatureXmlNode(model.features[node.id]);
				for each (var child: Object in node.children) {
					me.appendChild(nodeToXml(child));
				}
				return me;
			}
			
			var hierarchy: Object = ModelUtil.getHierarchy(model);
			for each (var node: Object: hierarchy.topNodes) {
				root.appendChild(nodeToXml(node));
			}

			_data = new XMLListCollection(root);
		}

		private function updateMinorChange(minorChange: Object): void {

		}

		public function WorkingModelView() {
		}

		[Bindable]
		public function get asXml(): XMLListCollection {
			return _data;
		}
		
		public function set asXml(val: XMLListCollection): void {
			this._data = val;
		}
		
		public function refresh(input: Object, minorChange: Object): void {
			if (minorChange) {
				updateEntireView(input);
			} else {
				updateMinorChange(input);
			}
			super.refresh(input, minorChange);
		}

	}
}