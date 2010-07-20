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
		   <descriptions><description>
		   <value></value>
		   <yes/>
		   <no/></description>
		   </descriptions>
		   <optional> <yes/> <no/> <optional>
		 *  </feature>

		 * <binary id=X type=Type left=Id1 right=Id2>
		   <yes/> <no/>
		 * </binary>
		 */

		private static const _defaultXml: XML = <model><feature/><binary/></model>;

		public static const IS_NEW_ELEMENT: String = "IsNewElement";
		public static const SHOULD_DELETE_ELEMENT: String = "ShouldDeleteElement";
		public static const FROM_OPPONENT_TO_SUPPORTER: String = "FromOpponentToSupporter";
		public static const VOTE_NO_TO_FEATURE: String = "VoteNoToFeature";

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
			ClientEvtDispatcher.instance().addEventListener(
				AddCommentEvent.SUCCESS, onCommentAdded);
		}

		public function registerSubView(view: IOperationListener): void {
			_subViews.push(view);
		}
		
		public function getRefinementId(parent: String, child: String): String {
			var rs: XMLList = this.binaries.source.(@type==Cst.BIN_REL_REFINES && @left==parent && @right==child);
			if (rs.length() > 0) {
				return rs.@id;
			}
			return null;
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

		public function isRelationshipOpponent(relationshipId: String): Boolean {
			var me: String = String(UserList.instance.myId);
			return findVoter(this.binaries, relationshipId, me, false);
		}
		
		public function getFeatureSupportRate(id: String): Number {
			var elements: XMLList = this.features.source.(@id==id);
			if (elements.length() > 0) {
				return this.getSupportRate(elements[0]);
			}
			return 0;
		}
		
		public function getRelationshipSupportRate(id: String): Number {
			var elements: XMLList = this.binaries.source.(@id==id);
			if (elements.length() > 0) {
				return this.getSupportRate(elements[0]);
			}
			return 0;
			// TODO: extend for group/complex relationships
		}
		
		public function getSupportRate(element: XML): Number {
			var y: int = XMLList(element.yes.user).length();
			var n: int = XMLList(element.no.user).length();
			var sr: Number = (n <= 0) ? 1 : (y / (y+n));
			return sr;
		}
		
		private function findVoter(target: XMLListCollection, id: String, voter: String, yes: Boolean): Boolean {
			if (yes) {
				return XMLList(target.source.(@id==id).yes.user.(text().toString()==voter)
					).length() > 0;
			}
			return XMLList(target.source.(@id==id).no.user.(text().toString()==voter)
				).length() > 0;
		}


		public function resetAll(): void {
			features.source = new XMLList(_defaultXml.feature);
			binaries.source = new XMLList(_defaultXml.binary);
		}
		
		private function onCommentAdded(evt: AddCommentEvent): void {
			var fs: XMLList = this.features.source.(@id==String(evt.feature));
			if (fs.length() > 0) {
				XML(fs[0].comments).insertChildAfter(null, 
					<comment creator={evt.user} time={evt.time}>
						{evt.content}
					</comment>);
			}
		}
		
		// For details about operations, see server.bean.operation package.
		private function onOperationCommit(evt: OperationCommitEvent): void {
			for each (var op: Object in evt.operations) {
				var opModel: String = op["modelId"];
				if (ModelCollection.instance.currentModelId != int(opModel)) {
					return;
				}
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
				for (var cursor: IViewCursor = features.createCursor(); !cursor.afterLast; cursor.moveNext()) {
					if (cursor.current.@id == String(o)) {
						if (ModelUtil.updateVoters(op["vote"], op["userid"], XML(cursor.current)) == false) {
							cursor.remove();
							// Record the target which needs to be deleted.
							if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == null) {
								op[FeatureModel.SHOULD_DELETE_ELEMENT] = new Array();
							}
							(op[FeatureModel.SHOULD_DELETE_ELEMENT] as Array).push(String(o));
						}
						break;
					}
				}
			}
		}

		public function handleRelationshipVotePropagation(op: Object): void {
			for each (var o: Object in op["targetIds"]) {
				for (var cursor: IViewCursor = binaries.createCursor(); !cursor.afterLast; cursor.moveNext()) {
					if (cursor.current.@id == String(o)) {
						if (ModelUtil.updateVoters(op["vote"], op["userid"], XML(cursor.current)) == false) {
							var info: Object = {
								id: cursor.current.@id,
								type: cursor.current.@type,
								left: cursor.current.@left,
								right: cursor.current.@right
							};
							cursor.remove();
							// Record the relationship which needs to be deleted.
							if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == null) {
								op[FeatureModel.SHOULD_DELETE_ELEMENT] = new Array();
							}
							
							(op[FeatureModel.SHOULD_DELETE_ELEMENT] as Array).push(info);
						}
						break;
					}
				}
			}
		}

		public function handleAddDescription(op:Object): void {
			var des: XMLList = features.source..description.(value.text().toString()==op["value"]);
			// if voting
			if (des.length() > 0) {
				if (ModelUtil.updateVoters(op["vote"], op["userid"], XML(des[0])) == false) {
					delete features.source..description.(value.text().toString()==op["value"])[0];
					op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
				}
				return;
			}
			// if creation
			op[FeatureModel.IS_NEW_ELEMENT] = true;

			XML(features.source.(@id==op["featureId"]).descriptions[0]).appendChild(
				<description>
					<value>{op["value"]}</value>
					<yes><user>{op["userid"]}</user></yes>
					<no/>
				</description>
				);

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
					// If voting NO, then vote NO to all names and descriptions, and remove vote from opt
					else if (String(op["vote"]).toLowerCase() == (new Boolean(false).toString().toLowerCase())) {
						op[FeatureModel.VOTE_NO_TO_FEATURE] = true;
						for each (var n: Object in cursor.current.names.name) {
							ModelUtil.updateVoters("false", op["userid"], XML(n));
						}
						// delete all-NO names
						var _removedNames: XMLList = cursor.current.names.name;
						for (var i: int = _removedNames.length()-1; i >= 0; i--) {
							if (XMLList(_removedNames[i].yes.user).length() == 0) {
								delete _removedNames[i];
							}
						}

						for each (var d: Object in cursor.current.descriptions.description) {
							ModelUtil.updateVoters("false", op["userid"], XML(d));
						}
						// delete all-NO descriptions
						var _removedDes: XMLList = cursor.current.descriptions.description;
						for (var i2: int = _removedDes.length()-1; i2 >= 0; i2--) {
							if (XMLList(_removedDes[i2].yes.user).length() == 0) {
								delete _removedDes[i2];
							}
						}
					}
					return;
				}
			}
			
			// If we reach here, it is a creating operation.
			op[FeatureModel.IS_NEW_ELEMENT] = true;

			features.addItem(<feature id={op["featureId"]} creator={op["userid"]}>
					<yes><user>{op["userid"]}</user></yes>
					<no/>
					<names>
						<name val={op["value"]}>
							<yes><user>{op["userid"]}</user></yes>
							<no/>
						</name>
					</names>
					<descriptions/>
					<comments/>
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
					var opposed: Boolean = isRelationshipOpponent(String(op["relationshipId"]));
					// A voting operation
					if (ModelUtil.updateVoters(op["vote"], op["userid"], XML(cursor.current)) == false) {
						cursor.remove();
						op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
					}
					if (opposed && ModelUtil.isTrue(op["vote"])) {
						op[FeatureModel.FROM_OPPONENT_TO_SUPPORTER] = true;
					}
					return;

				}
			}
			// A creating operation
			op[FeatureModel.IS_NEW_ELEMENT] = true;

			binaries.addItem(<binary id={op["relationshipId"]}
					creator={op["userid"]}
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

			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelUpdateEvent(ModelUpdateEvent.LOCAL_MODEL_COMPLETE, null));
			
		}


		private function createXmlFromFeature(f: Object): XML {
			var result: XML = <feature id={f.id} creator={f.cid} />;
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
				var curDes: XML = <description>
						<value>{d.val}</value>
					</description>;
				appendVotes(curDes, d);
				descs.appendChild(curDes);
			}

			// Optionality of the feature
			var optional: XML = <optional/>;
			appendVotes(optional, f, "opt1", "opt0");
			
			// Comments of the feature
			var cs: XML = <comments/>;
			for each (var c: Object in f.comments) {
				cs.appendChild(<comment creator={c.cid} time={c.time}>{c.content}</comment>);
			}
			
			result.appendChild(names);
			result.appendChild(descs);
			result.appendChild(optional);
			result.appendChild(cs);
			
			return result;
		}

		private function createXmlFromBinary(b: Object): XML {
			var result: XML = 
				<binary id={b.id} creator={b.cid} type={b.type} left={b.left} right={b.right}/>;
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
		
		public function stats(): String {
			// Number of features
			var numFeature: int = this.features.source.length();
			// Number of common features (NO vote == 0)
			var numCommon: int = 0;
			// Number of optional features (NO vote == 1, 2, 3, ...)
			var numOpt: Array = [];
			
			for each (var f: Object in this.features.source) {
				var n: int = XMLList(f.no.user).length();
				if (n == 0) {
					numCommon++;
				} else {
					if (numOpt[n] == undefined) {
						numOpt[n] = 1;
					} else {
						numOpt[n] = numOpt[n] + 1;
					}
				}
			}
			
			var s: String = "FeatureModel - Total: " + numFeature + " features; Common: " +
				numCommon + "; ";
			for (var a: Object in numOpt) {
				s += "NO by " + a + " user(s): " + numOpt[a] + " features; ";
			}
			return s;
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