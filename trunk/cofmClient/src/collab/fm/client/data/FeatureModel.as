package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import mx.collections.IViewCursor;
	import mx.collections.XMLListCollection;

	// The data of current feature model
	public class FeatureModel extends OperationListener {
		/*
		 * See server.data.protocol.UpdateResponse for details about the data structure.
		 *	<feature id=X>
		   <yes><user></user></yes>
		   <no><user></user></no>
		   <names>
		   <name val=TheName>
		   <yes/>
		   <no/>
		   </name>
		   </names>
		   <descriptions/>
		   <optional> <yes/> <no/> <optional>
		 *  </feature>

		 * <binary id=X type=Type left=Id1 right=Id2>
		   <yes/> <no/>
		 * </binary>
		 */

		private static const _defaultXml: XML = <model><feature/><binary/></model>;

		private var _features: XMLListCollection;
		private var _binaries: XMLListCollection;

		private static var _instance: FeatureModel = new FeatureModel();

		public static function get instance(): FeatureModel {
			return _instance;
		}

		public function FeatureModel() {
			super();
			_features = new XMLListCollection(new XMLList(_defaultXml.feature));
			_binaries = new XMLListCollection(new XMLList(_defaultXml.binary));

			ClientEvtDispatcher.instance().addEventListener(ModelUpdateEvent.SUCCESS, onModelUpdate);
		}

		override protected function handleAddDescription(op:Object): void {

		}

		override protected function handleAddName(op:Object): void {

		}

		override protected function handleCreateFeature(op:Object): void {
			for (var cursor: IViewCursor = features.createCursor(); !cursor.afterLast; cursor.moveNext()) {
				if (cursor.current.@id == op["featureId"]) {
					// A voting operation
					ModelUtil.updateVoters(op["vote"], op["userid"], XML(cursor.current));
					return;
				}
			}
			// If we reach here, it is a creating operation.
			features.addItem(<feature id={op["featureId"]}>
					<yes><user>{op["userid"]}</user></yes>
					<no/>
					<names>
						<name val={op["value"]}>
							<yes><user>{op["userid"]}</user></yes>
							<no/>
						</name>
					</names>
					<descriptions/>
					<optional><yes/><no/></optional>
				</feature>);
		}

		override protected function handleCreateRelationship(op:Object): void {

		}

		override protected function handleSetOpt(op:Object): void {

		}

		private function onModelUpdate(evt: ModelUpdateEvent): void {
			var fs: XML = <features/>;
			var bs: XML = <binaries/>;
			for each (var feature: Object in(evt.model.features as Array)) {
				fs.appendChild(createXmlFromFeature(feature));
			}
			for each (var binary: Object in(evt.model.binaries as Array)) {
				bs.appendChild(createXmlFromBinary(binary));
			}
			features.source = fs.feature;
			binaries.source = bs.binary;

			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelUpdateEvent(ModelUpdateEvent.LOCAL_MODEL_COMPLETE, null));
		}

		private function createXmlFromFeature(f: Object): XML {
			var result: XML = <feature id={f.id} />;
			// Votes on the feature
			appendVotes(result, f);

			// Names of the feature
			var names: XML = <names/>;
			for each (var n: Object in f.names) {
				var curName: XML = <name val={n.val}/>;
				appendVotes(curName, n);
				names.appendChild(curName);
			}

			// Desciptions of the feature
			var descs: XML = <descriptions/>;
			for each (var d: Object in f.dscs) {
				var curDes: XML = <description val={d.val}/>;
				appendVotes(curDes, d);
				descs.appendChild(curDes);
			}

			// Optionality of the feature
			var optional: XML = <optional/>;
			appendVotes(optional, f, "opt1", "opt0");

			result.appendChild(names);
			result.appendChild(descs);
			result.appendChild(optional);

			return result;
		}

		private function createXmlFromBinary(b: Object): XML {
			var result: XML = 
				<binary id={b.id} type={b.type} left={b.left} right={b.right}/>;
			appendVotes(result, b);
			return result;
		}

		private function appendVotes(root: XML, v: Object, fieldYes: String="v1", fieldNo: String="v0"): void {
			var yes: XML = <yes/>;
			for each (var u: Object in v[fieldYes]) {
				yes.appendChild(<user>{u}</user>);
			}
			var no: XML = <no/>;
			for each (var u1: Object in v[fieldNo]) {
				no.appendChild(<user>{u1}</user>);
			}
			root.appendChild(yes);
			root.appendChild(no);
		}

		[Bindable]
		public function get features(): XMLListCollection {
			return _features;
		}

		public function set features(xml: XMLListCollection): void {
			_features = xml;
		}

		[Bindable]
		public function get binaries(): XMLListCollection {
			return _binaries;
		}

		public function set binaries(xml: XMLListCollection): void {
			_binaries = xml;
		}
	}
}