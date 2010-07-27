package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import mx.collections.XMLListCollection;

	// The data of current feature model
	public class FeatureModel implements IOperationListener {
		/*
		 * See server.data.protocol.UpdateRequest.UpdateResponse for details about the data structure.
		 *	<feature id= creator= time=>
		   <yes><user></user></yes>
		   <no><user></user></no>
		   <comments> <comment/>
		   </comments>
		   <attrs>
		   <attr name= type= multi= dup= >
		   <values>
		   <value creator= time=>
		   <str> ... </str>
		   <yes> <user></user> </yes>
		   <no> <user></user> </no>
		   </value>
		   </values>
		   </attr>
		   </attrs>
		   </feature>

		 * <attr name=... type=... multi=... dup=...>
		   <enums...>  <min, max, unit>
		   </attr>

		 * <binary id=X type=Type left=Id1 right=Id2>
		   <yes/> <no/>
		 * </binary>
		 */

		private static const _defaultXml: XML = <model><feature/><binary/></model>;
		private static const _defaultAttrs: XML = <attrs/>;

		public static const IS_NEW_ELEMENT: String = "IsNewElement";
		public static const SHOULD_DELETE_ELEMENT: String = "ShouldDeleteElement";
		public static const INFERRED_REMOVAL_ELEMENTS: String = "InferredRemovalElements";
		public static const FROM_OPPONENT_TO_SUPPORTER: String = "FromOpponentToSupporter";
		public static const VOTE_NO_TO_FEATURE: String = "VoteNoToFeature";

		private var _features: XMLListCollection;
		private var _binaries: XMLListCollection;
		private var _attrs: XMLListCollection;

		private var _subViews: Array = new Array();

		private static var _instance: FeatureModel = new FeatureModel();

		public static function get instance(): FeatureModel {
			return _instance;
		}

		public function FeatureModel() {
			_features = new XMLListCollection(new XMLList(_defaultXml.feature));
			_binaries = new XMLListCollection(new XMLList(_defaultXml.binary));
			_attrs = new XMLListCollection(new XMLList(_defaultAttrs.attr));

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

		public function getValuesOfAttr(feature: XML, attrName: String): XMLList {
			return feature.attrs.attr.(@name==attrName).values.value;
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
			attrs.source = new XMLList(_defaultAttrs);
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
			var op: Object = evt.response;
			var opModel: String = op["modelId"];
			if (ModelCollection.instance.currentModelId != int(opModel)) {
				return;
			}
			if (evt.type == OperationCommitEvent.COMMIT_SUCCESS) {
				op["local"] = true;
			}
			switch (op[Cst.FIELD_RSP_SOURCE_NAME]) {
				case Cst.REQ_VA_ATTR:
					this.handleAddAttribute(op);
					for each (var v: Object in _subViews) {
						IOperationListener(v).handleAddAttribute(op);
					}
					break;
				case Cst.REQ_VA_ATTR_ENUM:
					this.handleAddEnumAttribute(op);
					for each (var v0: Object in _subViews) {
						IOperationListener(v0).handleAddEnumAttribute(op);
					}
					break;
				case Cst.REQ_VA_ATTR_NUMBER:
					this.handleAddNumericAttribute(op);
					for each (var v1: Object in _subViews) {
						IOperationListener(v1).handleAddNumericAttribute(op);
					}
					break;
				case Cst.REQ_VA_FEATURE:
					handleVoteAddFeature(op);
					handleInferVoteOnRelation(op); // Vote/add feature may cause inferred votes on relations
					for each (var v2: Object in _subViews) {
						IOperationListener(v2).handleVoteAddFeature(op);
						IOperationListener(v2).handleInferVoteOnRelation(op);
					}
					break;
				case Cst.REQ_VA_BIN_REL:
					handleVoteAddBinRel(op);
					handleInferVoteOnFeature(op);
					for each (var v3: Object in _subViews) {
						IOperationListener(v3).handleVoteAddBinRel(op);
						IOperationListener(v3).handleInferVoteOnFeature(op);
					}
					break;
				case Cst.REQ_VA_VALUE:
					this.handleVoteAddValue(op);
					for each (var v4: Object in _subViews) {
						IOperationListener(v4).handleVoteAddValue(op);
					}
					break;
			}
		}

		/**
		 * Only "YES" votes ("true") can be inferred on features
		 */
		public function handleInferVoteOnFeature(op: Object): void {
			for each (var o: Object in op[Cst.FIELD_RSP_INFER_VOTES]) {
				var targets: XMLList = features.source.(@id==String(o));
				if (targets.length() > 0) {
					ModelUtil.updateVoters("true", op[Cst.FIELD_RSP_SOURCE_USER_ID], XML(targets[0]));
				}
			}
		}

		/**
		 * Only "NO" votes ("false") can be inferred on relations.
		 */
		public function handleInferVoteOnRelation(op: Object): void {
			for each (var o: Object in op[Cst.FIELD_RSP_INFER_VOTES]) {
				var targets: XMLList = binaries.source.(@id==String(o));
				if (targets.length() > 0) {
					var current: XML = targets[0];
					if (ModelUtil.updateVoters("false", op[Cst.FIELD_RSP_SOURCE_USER_ID], current) == false) {
						var info: Object = {
								id: current.@id,
								type: current.@type,
								left: current.@left,
								right: current.@right
							};
						// Record the relationship which needs to be deleted.
						if (op[FeatureModel.INFERRED_REMOVAL_ELEMENTS] == null) {
							op[FeatureModel.INFERRED_REMOVAL_ELEMENTS] = new Array();
						}

						(op[FeatureModel.INFERRED_REMOVAL_ELEMENTS] as Array).push(info);
						delete targets[0];
					}
				}
			}
		}

		public function handleVoteAddValue(op: Object): void {
			// TODO: handle the "multiple support" and "duplicate" options
			// Feature ID must not be null
			if (op["featureId"] == null) {
				return;
			}

			var curAttr: XML = null, curFeature: XML = null;
			var f: XMLList = features.source.(@id==op["featureId"]); // Find the feature with specific ID.
			if (f.length() <= 0) {
				return;
			}
			curFeature = f[0];
			var a: XMLList = curFeature..attr.(@name==op["attr"]); // then find the specific attribute in this feature
			if (a.length() <= 0) {
				// No such attribute, create it first
				var attrSet: XMLList = this.attrs.source.(@name==op["attr"]);
				if (attrSet.length() > 0) {
					curAttr = XML(attrSet[0]).copy();
					curAttr.appendChild(<values/>);
					curFeature.appendChild(curAttr);
				} else {
					return;
				}
			} else {
				curAttr = a[0];
			}
			var targets: XMLList = curAttr.values.value.(str.text().toString() == op["val"]); // Find the value
			if (targets.length() > 0) {
				// A voting operation
				if (ModelUtil.updateVoters(op[Cst.FIELD_RSP_VOTE], 
					op[Cst.FIELD_RSP_SOURCE_USER_ID], XML(targets[0])) == false) {
					delete targets[0];
					op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
				}
			} else {
				// A creating operation
				op[FeatureModel.IS_NEW_ELEMENT] = true;
				XML(a[0].values).appendChild(<value creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}>
						<str>{op["val"]}</str>
						<yes><user>{op[Cst.FIELD_RSP_SOURCE_USER_ID]}</user></yes>
						<no/>
					</value>);
			}
		}

		/**
		 * Add String/Text/List attribute
		 */
		public function handleAddAttribute(op: Object): void {
			if (XMLList(this.attrs.source.(@name==op["attr"])).length() <= 0) {
				this.attrs.addItem(<attr name={op["attr"]}
						type={op["type"]}
						multi={op["multiYes"]}
						dup={op["allowDup"]}/>
					);
			}
		}

		public function handleAddEnumAttribute(op: Object): void {
			if (XMLList(this.attrs.source.(@name==op["attr"])).length() <= 0) {
				var a: XML = <attr name={op["attr"]}
						type={op["type"]}
						multi={op["multiYes"]}
						dup={op["allowDup"]}/>;
				var xmlEnum: XML = <enums/>;
				for each (var en: Object in op.vlist) {
					xmlEnum.appendChild(<enum>{en}</enum>);
				}
				a.appendChild(xmlEnum);
				this.attrs.addItem(a);
			}
		}

		public function handleAddNumericAttribute(op: Object): void {
			if (XMLList(this.attrs.source.(@name==op["attr"])).length() <= 0) {
				this.attrs.addItem(<attr name={op["attr"]}
						type={op["type"]}
						multi={op["multiYes"]}
						dup={op["allowDup"]}>
						<min>{op.min}</min>
						<max>{op.max}</max>
						<unit>{op.unit}</unit>
					</attr>);
			}
		}

		public function handleVoteAddFeature(op:Object): void {
			var targets: XMLList = features.source.(@id==op["featureId"]);
			if (targets.length() > 0) {
				// A voting operation
				var current: XML = targets[0];
				if (ModelUtil.updateVoters(op[Cst.FIELD_RSP_VOTE], 
					op[Cst.FIELD_RSP_SOURCE_USER_ID], current) == false) {
					delete targets[0];
					op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
				} else if (ModelUtil.isTrue(op[Cst.FIELD_RSP_VOTE]) == false) {
					op[FeatureModel.VOTE_NO_TO_FEATURE] = true;
				}
			} else {
				op[FeatureModel.IS_NEW_ELEMENT] = true;

				features.addItem(<feature id={op["featureId"]} creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}>
						<yes><user>{op[Cst.FIELD_RSP_SOURCE_USER_ID]}</user></yes>
						<no/>
						<attrs>
							<attr name={Cst.ATTR_FEATURE_NAME} type={Cst.ATTR_TYPE_STRING} multi="true" dup="false">
								<values>
									<value creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}>
										<str>{op["featureName"]}</str>
										<yes><user>op[Cst.FIELD_RSP_SOURCE_USER_ID]</user></yes>
										<no/>
									</value>
								</values>
							</attr>
							<attr name={Cst.ATTR_FEATURE_DES} type={Cst.ATTR_TYPE_TEXT} multi="true" dup="true">
								<values/>
							</attr>
							<attr name={Cst.ATTR_FEATURE_OPT} type={Cst.ATTR_TYPE_ENUM} multi="false" dup="true">
								<values/>
								<enums>
									<enum>{Cst.VAL_OPT_MAN}</enum>
									<enum>{Cst.VAL_OPT_OPT}</enum>
								</enums>
							</attr>
						</attrs>
						<comments/>
					</feature>);

				if (op["local"] != null) {
					ClientEvtDispatcher.instance().dispatchEvent(
						new ModelMinorChangeEvent(ModelMinorChangeEvent.FEATURE_CREATED_LOCALLY, op["featureId"]));
				}
			}
		}

		public function handleVoteAddBinRel(op:Object): void {
			var targets: XMLList = binaries.source.(@id==op["relationshipId"]);
			if (targets.length() > 0) {
				// A voting operation
				var current: XML = targets[0];
				var opposed: Boolean = isRelationshipOpponent(String(op["relationshipId"]));
				if (ModelUtil.updateVoters(op[Cst.FIELD_RSP_VOTE], 
					op[Cst.FIELD_RSP_SOURCE_USER_ID], current) == false) {
					delete targets[0];
					op[FeatureModel.SHOULD_DELETE_ELEMENT] = true;
				}
				if (opposed && ModelUtil.isTrue(op[Cst.FIELD_RSP_VOTE])) {
					op[FeatureModel.FROM_OPPONENT_TO_SUPPORTER] = true;
				}
			} else {
				// A creating operation

				op[FeatureModel.IS_NEW_ELEMENT] = true;

				// TODO: add creation time
				binaries.addItem(<binary id={op["relationshipId"]}
						creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
						type={op["type"]}
						left={op["leftFeatureId"]}
						right={op["rightFeatureId"]}>
						<yes><user>{op[Cst.FIELD_RSP_SOURCE_USER_ID]}</user></yes>
						<no/>
					</binary>
					);

			}
		}

		private function onModelUpdate(evt: ModelUpdateEvent): void {

			var fs: XML = <features/>;
			var bs: XML = <binaries/>;
			var atts: XML = <attrs/>;
			for each (var feature: Object in(evt.model.features as Array)) {
				fs.appendChild(createXmlFromFeature(feature));
			}
			for each (var binary: Object in(evt.model.binaries as Array)) {
				bs.appendChild(createXmlFromBinary(binary));
			}
			for each (var a: Object in(evt.model.attrs as Array)) {
				trace ("Update attribute - " + a.name);
				appendAttr(atts, a, false);
			}
			features.source = fs.feature;
			binaries.source = bs.binary;
			attrs.source = atts.attr;


			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelUpdateEvent(ModelUpdateEvent.LOCAL_MODEL_COMPLETE, null));

		}


		private function createXmlFromFeature(f: Object): XML {
			var result: XML = <feature id={f.id} creator={f.cid} time={f.ctime} />;
			// Votes on the feature
			appendVotes(result, f);

			// Attributes of the feature (also write to attribute-set (this.attrs))
			var attrs: XML = <attrs/>;
			for each (var a: Object in f.attrs) {
				appendAttr(attrs, a);
			}
			result.appendChild(attrs);

			// Comments of the feature
			var cs: XML = <comments/>;
			for each (var c: Object in f.comments) {
				cs.appendChild(<comment creator={c.cid} time={c.ctime}>{c.content}</comment>);
			}
			result.appendChild(cs);

			return result;
		}

		private function createXmlFromBinary(b: Object): XML {
			var result: XML = 
				<binary id={b.id} creator={b.cid} time={b.ctime} type={b.type} left={b.left} right={b.right}/>;
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

		private function appendAttr(root: XML, obj: Object, needValues: Boolean = true): void {
			var attr: XML = <attr name={obj.name} type={obj.type} multi={obj.multi} dup={obj.dup} />;

			// Append other parts of "Enum-Attributes" and "Numeric-Attributes"
			switch (String(obj["type"])) {
				case Cst.ATTR_TYPE_ENUM:
					var xmlEnum: XML = <enums/>;
					for each (var en: Object in obj.enums) {
						xmlEnum.appendChild(<enum>{String(en)}</enum>);
					}
					attr.appendChild(xmlEnum);
					break;
				case Cst.ATTR_TYPE_NUMBER:
					attr.appendChild(<min>{obj.min}</min>);
					attr.appendChild(<max>{obj.max}</max>);
					attr.appendChild(<unit>{obj.unit}</unit>);
					break;
			}

			if (needValues) {
				// Append values of the attributes (The attribute-set doesn't contain the values)
				var vs: XML = <values/>
				for each (var val: Object in obj.vals) {
					var xmlVal: XML = <value creator={val.cid} time={val.ctime}><str>{val.val}</str></value>;
					appendVotes(xmlVal, val);
					vs.appendChild(xmlVal);
				}
				attr.appendChild(vs);
			}

			root.appendChild(attr);
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

		[Bindable]
		public function get attrs(): XMLListCollection {
			return _attrs;
		}

		public function set attrs(xml: XMLListCollection): void {
			_attrs = xml;
		}
	}
}