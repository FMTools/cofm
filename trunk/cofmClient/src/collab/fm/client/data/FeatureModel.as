package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import mx.collections.IViewCursor;
	import mx.collections.XMLListCollection;

	// The data of current feature model
	public class FeatureModel implements IOperationListener {
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

		public static const IS_NEW_ELEMENT: String = "IsNewElement";
		public static const SHOULD_DELETE_ELEMENT: String = "ShouldDeleteElement";

		private var _features: XMLListCollection;
		private var _binaries: XMLListCollection;

		private var _subViews: Array = new Array();

		private static var _instance: FeatureModel = new FeatureModel();

		public static function get instance(): FeatureModel {
			return _instance;
		}

		public function FeatureModel() {
			_features = new XMLListCollection(new XMLList(_defaultXml.feature));
			_binaries = new XMLListCollection(new XMLList(_defaultXml.binary));

			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.SUCCESS, onModelUpdate);
			ClientEvtDispatcher.instance().addEventListener(
				OperationCommitEvent.COMMIT_SUCCESS, onOperationCommit);
			ClientEvtDispatcher.instance().addEventListener(
				OperationCommitEvent.FORWARDED, onOperationCommit);
		}

		public function registerSubView(view: IOperationListener): void {
			_subViews.push(view);
		}

		public function containsFeature(featureId: String): Boolean {
			var fs: XMLList = features.source.(@id==featureId);
			return fs.length() > 0;
		}

		public function containsRelationship(relationshipId: String): Boolean {
			var rs: XMLList = binaries.source.(@id==relationshipId);
			return rs.length() > 0;
			// TODO: extend if there are other types of relationships (group or complex)
		}

		public function resetAll(): void {
			features.source = new XMLList(_defaultXml.feature);
			binaries.source = new XMLList(_defaultXml.binary);
		}

		// For details about operations, see server.bean.operation package.
		private function onOperationCommit(evt: OperationCommitEvent): void {
			for each (var op: Object in evt.operations) {
				var opName: String = op["name"];
				if (evt.type == OperationCommitEvent.COMMIT_SUCCESS) {
					op["local"] = true;
				}
				switch (opName) {
					case Cst.OP_ADD_DES:
						handleAddDescription(op);
						for each (var v: Object in _subViews) {
							IOperationListener(v).handleAddDescription(op);
						}
						break;
					case Cst.OP_ADD_NAME:
						handleAddName(op);
						for each (var v1: Object in _subViews) {
							IOperationListener(v1).handleAddName(op);
						}
						break;
					case Cst.OP_CREATE_FEATURE:
						if (op["targetIds"] != null) {
							handleFeatureVotePropagation(op);
							for each (var v2_: Object in _subViews) {
								IOperationListener(v2_).handleFeatureVotePropagation(op);
							}
						} else {
							handleCreateFeature(op);
							for each (var v2: Object in _subViews) {
								IOperationListener(v2).handleCreateFeature(op);
							}
						}
						break;
					case Cst.OP_CREATE_RELATIONSHIP:
						if (op["targetIds"] != null) {
							handleRelationshipVotePropagation(op);
							for each (var v3_: Object in _subViews) {
								IOperationListener(v3_).handleRelationshipVotePropagation(op);
							}
						} else {
							handleCreateBinaryRelationship(op);
							for each (var v3: Object in _subViews) {
								IOperationListener(v3).handleCreateBinaryRelationship(op);
							}
						}
						break;
					case Cst.OP_SET_OPT:
						handleSetOpt(op);
						for each (var v4: Object in _subViews) {
							IOperationListener(v4).handleSetOpt(op);
						}
						break;
				}
			}
		}

		public function handleFeatureVotePropagation(op: Object): void {
			for each (var o: Object in op["targetIds"]) {
				var fs: XMLList = features.source.(@id==String(o));
				if (fs.length() > 0) {
					if (ModelUtil.updateVoters(op["vote"], op["userid"], fs[0]) == false) {
						delete features.source.(@id==String(o))[0];
						// Record the target which needs to be deleted.
						if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == null) {
							op[FeatureModel.SHOULD_DELETE_ELEMENT] = new Array();
						}
						(op[FeatureModel.SHOULD_DELETE_ELEMENT] as Array).push(String(o));
					}
				}
			}
		}

		public function handleRelationshipVotePropagation(op: Object): void {
			for each (var o: Object in op["targetIds"]) {
				var rs: XMLList = binaries.source.(@id==String(o));
				if (rs.length() > 0) {
					if (ModelUtil.updateVoters(op["vote"], op["userid"], rs[0]) == false) {
						delete features.source.(@id==String(o))[0];
						// Record the target which needs to be deleted.
						if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == null) {
							op[FeatureModel.SHOULD_DELETE_ELEMENT] = new Array();
						}
						(op[FeatureModel.SHOULD_DELETE_ELEMENT] as Array).push(String(o));
					}
				}
			}
		}

		public function handleAddDescription(op:Object): void {

		}

		public function handleAddName(op:Object): void {
			var theName: XMLList = features.source..name.(@val==op["value"]);
			// if voting
			if (theName.length() > 0) {
				if (ModelUtil.updateVoters(op["vote"], op["userid"], XML(theName[0])) == false) {
					delete features.source..name.(@val==op["value"])[0];
					op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
				}
				return;
			}
			// if creation
			op[FeatureModel.IS_NEW_ELEMENT] = true;

			// add the name to the feature
			XML(features.source.(@id==op["featureId"]).names[0]).appendChild(
				<name val={op["value"]}>
					<yes><user>{op["userid"]}</user></yes>
					<no/>
				</name>
				);
		}

		public function handleCreateFeature(op:Object): void {
			for (var cursor: IViewCursor = features.createCursor(); !cursor.afterLast; cursor.moveNext()) {
				if (cursor.current.@id == op["featureId"]) {
					// A voting operation
					if (ModelUtil.updateVoters(op["vote"], op["userid"], XML(cursor.current)) == false) {
						cursor.remove();
						op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
					}
					return;
				}
			}
			// If we reach here, it is a creating operation.
			op[FeatureModel.IS_NEW_ELEMENT] = true;

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

			if (op["local"] != null) {
				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelMinorChangeEvent(ModelMinorChangeEvent.FEATURE_CREATED_LOCALLY, op["featureId"]));
			}
		}

		public function handleCreateBinaryRelationship(op:Object): void {
			for (var cursor: IViewCursor = binaries.createCursor(); !cursor.afterLast; cursor.moveNext()) {
				if (cursor.current.@id == op["relationshipId"]) {
					// A voting operation
					if (ModelUtil.updateVoters(op["vote"], op["userid"], XML(cursor.current)) == false) {
						cursor.remove();
						op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
					}
					return;

				}
			}
			// A creating operation
			op[FeatureModel.IS_NEW_ELEMENT] = true;

			binaries.addItem(<binary id={op["relationshipId"]}
					type={op["type"]}
					left={op["leftFeatureId"]}
					right={op["rightFeatureId"]}>
					<yes><user>{op["userid"]}</user></yes>
					<no/>
				</binary>
				);
		}

		public function handleSetOpt(op:Object): void {

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

			trace("------FeatureModel.onModelUpdate--------");
			trace(features.source.toXMLString());
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