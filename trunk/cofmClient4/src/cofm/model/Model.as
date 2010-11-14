package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import mx.collections.XMLListCollection;
	
	// The data of current feature model
	public class Model implements IOperationListener {
		/*
		* See server.data.protocol.UpdateRequest.UpdateResponse for details about the data structure.
		*/
		
		private static const _defaultXml: XML = <model><entity/><binary/><entype/><bintype/></model>;
		
		public static const IS_NEW_ELEMENT: String = "IsNewElement";
		public static const IS_A_REFINEMENT: String = "IsARefinement";
		public static const SHOULD_DELETE_ELEMENT: String = "ShouldDeleteElement";
		public static const INFERRED_REMOVAL_ELEMENTS: String = "InferredRemovalElements";
		public static const FROM_OPPONENT_TO_SUPPORTER: String = "FromOpponentToSupporter";
		public static const VOTE_NO_TO_FEATURE: String = "VoteNoToFeature";
		
		[Bindable] public var entities: XMLListCollection;
		[Bindable] public var binaries: XMLListCollection;
		[Bindable] public var entypes: XMLListCollection;
		[Bindable] public var bintypes: XMLListCollection;
		
		private var _subViews: Array = new Array();
		
		private static var _instance: Model = new Model();
		
		public static function instance(): Model {
			return _instance;
		}
		
		public function Model() {
			entities = new XMLListCollection(new XMLList(_defaultXml.entity));
			binaries = new XMLListCollection(new XMLList(_defaultXml.binary));
			entypes = new XMLListCollection(new XMLList(_defaultXml.entype));
			bintypes = new XMLListCollection(new XMLList(_defaultXml.bintype));
			
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
		
		// Utility methods
		public function getValuesByAttrName(entity: XML, attrName: String): XMLList {
			var attrDefs: XMLList = this.entypes.source.(@id==entity.@typeId)[0]
				.attrDefs.attrDef.(@name==attrName);
			// get attrId by attrName
			if (attrDefs.length() <= 0) {
				return null;
			}
			var attrId: String = attrDefs[0].@id;
			return entity.attrs.attr.(@id==attrId).values.value;
		}
		
		public function getAttrNameById(entity: XML, attrId: String): String {
			var attrDefs: XMLList = this.entypes.source.(@id==entity.@typeId)[0]
				.attrDefs.attrDef.(@id==attrId);
			if (attrDefs.length() <= 0) {
				return null;
			}
			return attrDefs[0].@name;
		}
		
		public function getRefinementId(parent: String, child: String): String {
			// Get all binary relations in the form of "Parent Relation Child"
			var rs: XMLList = this.binaries.source.
				(@sourceId==parent && @targetId==child);
			if (rs.length() > 0) {
				for each (var r: Object in rs) {
					// return the relation which is a hierarchical type (i.e. a Refinement).
					if (isRefinement(XML(r))) {
						return r.@id;
					}
				}
			}
			return null;
		}
		
		public function isRefinement(binrelation: XML): Boolean {
			return this.isRefinementById(binrelation.@typeId);
		}
		
		public function isRefinementById(typeId: String): Boolean {
			var rTypes: XMLList = this.bintypes.source.(@id==typeId);
			if (rTypes.length() > 0 && rTypes[0].@hier == "true") {
				return true;
			}
			return false;
		}
		
		public function containsEntity(featureId: String): Boolean {
			var fs: XMLList = entities.source.(@id==featureId);
			return fs.length() > 0;
		}
		
		public function containsRelationship(relationshipId: String): Boolean {
			var rs: XMLList = binaries.source.(@id==relationshipId);
			return rs.length() > 0;
			// TODO: extend if there are other types of relationships (group or complex)
		}
		
		public function isRelationshipOpponent(relationshipId: String): Boolean {
			var me: String = String(UserList.instance().myId);
			return findVoter(this.binaries, relationshipId, me, false);
		}
		
		public function getEntitySupportRate(id: String): Number {
			var elements: XMLList = this.entities.source.(@id==id);
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
			entities.source = new XMLList(_defaultXml.entity);
			binaries.source = new XMLList(_defaultXml.binary);
			entypes.source = new XMLList(_defaultXml.entype);
			bintypes.source = new XMLList(_defaultXml.bintype);
		}
		
		private function onCommentAdded(evt: AddCommentEvent): void {
			var fs: XMLList = this.entities.source.(@id==String(evt.feature));
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
			if (ModelCollection.instance().currentModelId != int(opModel)) {
				return;
			}
			if (evt.type == OperationCommitEvent.COMMIT_SUCCESS) {
				op["local"] = true;
			}
			switch (op[Cst.FIELD_RSP_SOURCE_NAME]) {
				case Cst.REQ_VA_ATTR:
					this.handleEditAddAttributeDef(op);
					for each (var v: Object in _subViews) {
						IOperationListener(v).handleEditAddAttributeDef(op);
					}
					break;
				case Cst.REQ_VA_ATTR_ENUM:
					this.handleEditAddEnumAttributeDef(op);
					for each (var v0: Object in _subViews) {
						IOperationListener(v0).handleEditAddEnumAttributeDef(op);
					}
					break;
				case Cst.REQ_VA_ATTR_NUMBER:
					this.handleEditAddNumericAttributeDef(op);
					for each (var v1: Object in _subViews) {
						IOperationListener(v1).handleEditAddNumericAttributeDef(op);
					}
					break;
				case Cst.REQ_VA_ENTITY:
					handleVoteAddEntity(op);
					handleInferVoteOnRelation(op); // Vote/add feature may cause inferred votes on relations
					for each (var v2: Object in _subViews) {
						IOperationListener(v2).handleVoteAddEntity(op);
						IOperationListener(v2).handleInferVoteOnRelation(op);
					}
					break;
				case Cst.REQ_VA_BIN_REL:
					handleVoteAddBinRel(op);
					handleInferVoteOnEntity(op);
					for each (var v3: Object in _subViews) {
						IOperationListener(v3).handleVoteAddBinRel(op);
						IOperationListener(v3).handleInferVoteOnEntity(op);
					}
					break;
				case Cst.REQ_VA_VALUE:
					this.handleVoteAddValue(op);
					for each (var v4: Object in _subViews) {
						IOperationListener(v4).handleVoteAddValue(op);
					}
					break;
				case Cst.REQ_EA_ENTITY_TYPE:
					this.handleEditAddEntityType(op);
					for each (var v5: Object in _subViews) {
						IOperationListener(v5).handleEditAddEntityType(op);
					}
					break;
				case Cst.REQ_EA_BINREL_TYPE:
					this.handleEditAddBinRelType(op);
					for each (var v6: Object in _subViews) {
						IOperationListener(v6).handleEditAddBinRelType(op);
					}
					break;
			}
		}
		
		public function handleEditAddEntityType(op: Object): void {
			var _entypes: XMLList = this.entypes.source.(@id==op["typeId"]);
			if (_entypes.length() > 0) {
				// an editing
				_entypes[0].@name = op["typeName"];
				_entypes[0].@mid = op[Cst.FIELD_RSP_SOURCE_USER_ID];
				_entypes[0].@mtime= op["execTime"];
				return;
			}
			// an adding
			this.entypes.addItem(			
				<entype id={op["typeId"]} 
					creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]} 
					ctime={op["execTime"]} 
					mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]} 
					mtime={op["execTime"]}
					name={op["typeName"]}
					superId={op["superTypeId"]} >
					<attrDefs/>
				</entype>);
		}
		
		public function handleEditAddBinRelType(op: Object): void {
			var _bintypes: XMLList = this.bintypes.source.(@id==op["relId"]);
			if (_bintypes.length() > 0) {
				_bintypes[0].name = op["typeName"];
				_bintypes[0].@mid = op[Cst.FIELD_RSP_SOURCE_USER_ID];
				_bintypes[0].@mtime= op["execTime"];
				return;
			}
			this.bintypes.addItem(
				<bintype id={op["relId"]} 
				creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]} 
				ctime={op["execTime"]} 
				mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]} 
				mtime={op["execTime"]}
				name={op["typeName"]}
				hier={op["hierarchical"]}
				dir={op["directed"]}
				sourceTypeId={op["sourceId"]} 
				targetTypeId={op["targetId"]} />
			);
		}
		
		/**
		 * Only "YES" votes ("true") can be inferred on features
		 */
		public function handleInferVoteOnEntity(op: Object): void {
			for each (var o: Object in op[Cst.FIELD_RSP_INFER_VOTES]) {
				var targets: XMLList = entities.source.(@id==String(o));
				if (targets.length() > 0) {
					ModelUtil.updateVoters("true", op[Cst.FIELD_RSP_SOURCE_USER_ID], XML(targets[0]), op["execTime"]);
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
					if (ModelUtil.updateVoters("false", op[Cst.FIELD_RSP_SOURCE_USER_ID], current, op["execTime"]) == false) {
						var info: Object = current.copy();
						// Record the relationship which needs to be deleted.
						if (op[Model.INFERRED_REMOVAL_ELEMENTS] == null) {
							op[Model.INFERRED_REMOVAL_ELEMENTS] = new Array();
						}
						
						(op[Model.INFERRED_REMOVAL_ELEMENTS] as Array).push(info);
						delete targets[0];
					}
				}
			}
		}
		
		/**
		 * Add String/Text attribute
		 */
		public function handleEditAddAttributeDef(op: Object): void {
			var def: XML = <attrDef id={op["attrId"]} 
				creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
				ctime={op["execTime"]} 
				mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
				mtime={op["execTime"]}
				name={op["attr"]} type={op["type"]} 
				multi={op["multiYes"]} dup={op["allowDup"]}>
			</attrDef>;
			
			editOrAddAttributeDef(op, def);
		}
		
		public function handleEditAddEnumAttributeDef(op: Object): void {
			var def: XML = <attrDef id={op["attrId"]} 
				creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
				ctime={op["execTime"]} 
				mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
				mtime={op["execTime"]}
				name={op["attr"]} type={op["type"]} 
				multi={op["multiYes"]} dup={op["allowDup"]}>
			</attrDef>;
			
			var xmlEnum: XML = <enums/>;
			for each (var en: Object in op.vlist) {
				xmlEnum.appendChild(<enum>{en}</enum>);
			}
			def.appendChild(xmlEnum);
			
			editOrAddAttributeDef(op, def);
		}
		
		public function handleEditAddNumericAttributeDef(op: Object): void {
			var def: XML = <attrDef id={op["attrId"]} 
				creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
				ctime={op["execTime"]} 
				mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
				mtime={op["execTime"]}
				name={op["attr"]} type={op["type"]} 
				multi={op["multiYes"]} dup={op["allowDup"]}>
				<min>{op.min}</min>
				<max>{op.max}</max>
				<unit>{op.unit}</unit>
			</attrDef>;
			
			editOrAddAttributeDef(op, def);
		}
		
		private function editOrAddAttributeDef(op: Object, attrDef: XML): void {
			var entypes: XMLList = this.entypes.source.(@id==op["entityTypeId"]);
			if (entypes.length() <= 0) {
				return;  // unknown entity type
			}
			var defs: XMLList = entypes[0]..attrDef.(@id==op["attrId"]);
			if (defs.length() > 0) {
				// an editing
				defs[0].@name = op["attr"];
				defs[0].@mid = op[Cst.FIELD_RSP_SOURCE_USER_ID];
				defs[0].@mtime= op["execTime"];
				return;
			}
			// an adding
			XML(entypes[0].attrDefs).appendChild(attrDef);
		}
		
		public function handleVoteAddValue(op: Object): void {
			// TODO: handle the "multiple support" and "duplicate" options
			// Feature ID must not be null
			if (op["entityId"] == null) {
				return;
			}
			
			var curAttr: XML = null, curFeature: XML = null;
			var f: XMLList = entities.source.(@id==op["entityId"]); // Find the feature with specific ID.
			if (f.length() <= 0) {
				return;
			}
			curFeature = f[0];
			var a: XMLList = curFeature..attr.(@id==op["attrId"]); // then find the specific attribute in this feature
			if (a.length() <= 0) {
				// No such attribute, create it first
				curAttr = <attr id={op["attrId"]}><values/></attr>;
				curFeature.appendChild(curAttr);
			} else {
				curAttr = a[0];
			}
			var targets: XMLList = curAttr.values.value.(str.text().toString() == op["val"]); // Find the value
			if (targets.length() > 0) {
				// A voting operation
				if (ModelUtil.updateVoters(op[Cst.FIELD_RSP_VOTE], 
					op[Cst.FIELD_RSP_SOURCE_USER_ID], XML(targets[0]), op["execTime"]) == false) {
					delete targets[0];
					op[Model.SHOULD_DELETE_ELEMENT] = true;
				}
			} else {
				// A creating operation
				op[Model.IS_NEW_ELEMENT] = true;
				XML(curAttr.values).appendChild(<value 
					creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
					mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
					ctime={op["execTime"]}
					mtime={op["execTime"]} >
						<str>{op["val"]}</str>
						<yes><user>{op[Cst.FIELD_RSP_SOURCE_USER_ID]}</user></yes>
						<no/>
					</value>);
			}
		}
		
		public function handleVoteAddEntity(op:Object): void {
			var targets: XMLList = entities.source.(@id==op["entityId"]);
			if (targets.length() > 0) {
				// A voting operation
				var current: XML = targets[0];
				if (ModelUtil.updateVoters(op[Cst.FIELD_RSP_VOTE], 
					op[Cst.FIELD_RSP_SOURCE_USER_ID], current, op["execTime"]) == false) {
					delete targets[0];
					op[Model.SHOULD_DELETE_ELEMENT] = true;
				} else if (ModelUtil.isTrue(op[Cst.FIELD_RSP_VOTE]) == false) {
					op[Model.VOTE_NO_TO_FEATURE] = true;
				}
			} else {
				op[Model.IS_NEW_ELEMENT] = true;
				
				entities.addItem(<entity id={op["entityId"]} 
					creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
					mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
					ctime={op["execTime"]}
					mtime={op["execTime"]}
					typdId={op["typeId"]} >
						<yes><user>{op[Cst.FIELD_RSP_SOURCE_USER_ID]}</user></yes>
						<no/>
						<attrs/>
						<comments/>
					</entity>);
				
				if (op["local"] != null) {
					ClientEvtDispatcher.instance().dispatchEvent(
						new ModelMinorChangeEvent(ModelMinorChangeEvent.FEATURE_CREATED_LOCALLY, op["entityId"]));
				}
			}
		}
		
		public function handleVoteAddBinRel(op:Object): void {
			var targets: XMLList = binaries.source.(@id==op["relationId"]);
			if (targets.length() > 0) {
				// A voting operation
				var current: XML = targets[0];
				var opposed: Boolean = isRelationshipOpponent(String(op["relationId"]));
				if (ModelUtil.updateVoters(op[Cst.FIELD_RSP_VOTE], 
					op[Cst.FIELD_RSP_SOURCE_USER_ID], current,
					op["execTime"]) == false) {
					delete targets[0];
					op[Model.SHOULD_DELETE_ELEMENT] = true;
				}
				if (opposed && ModelUtil.isTrue(op[Cst.FIELD_RSP_VOTE])) {
					op[Model.FROM_OPPONENT_TO_SUPPORTER] = true;
				}
			} else {
				// A creating operation
				
				op[Model.IS_NEW_ELEMENT] = true;
				
				binaries.addItem(<binary id={op["relationId"]}
					creator={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
					mid={op[Cst.FIELD_RSP_SOURCE_USER_ID]}
					ctime={op["execTime"]}
					mtime={op["execTime"]}
					typeId={op["typeId"]}
					sourceId={op["sourceId"]}
					targetId={op["targetId"]}>
					<yes><user>{op[Cst.FIELD_RSP_SOURCE_USER_ID]}</user></yes>
					<no/>
				</binary>
				);
			}
			
			op[Model.IS_A_REFINEMENT] = this.isRefinementById(op["typeId"]);
		}
		
		private function onModelUpdate(evt: ModelUpdateEvent): void {
			
			var fs: XML = <entities/>;
			var bs: XML = <binaries/>;
			var ets: XML = <entypes/>;
			var bts: XML = <bintypes/>;

			for each (var e: Object in (evt.model.entities as Array)) {
				fs.appendChild(createXmlFromFeature(e));
			}
			for each (var binary: Object in (evt.model.binaries as Array)) {
				bs.appendChild(createXmlFromBinary(binary));
			}
			for each (var entype: Object in (evt.model.entypes as Array)) {
				ets.appendChild(createXmlFromEntype(entype));
			}
			for each (var bintype: Object in (evt.model.bintypes as Array)) {
				bts.appendChild(createXmlFromBintype(bintype));
			}
			
			entities.source = fs.entity;
			binaries.source = bs.binary;
			entypes.source = ets.entype;
			bintypes.source = bts.bintype;
			
			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelUpdateEvent(ModelUpdateEvent.LOCAL_MODEL_COMPLETE, null));
			
		}
		
		
		private function createXmlFromFeature(f: Object): XML {
			var result: XML = <entity id={f.id} creator={f.cid} ctime={f.ctime}
					mid={f.mid} mtime={f.mtime} typeId={f.typeId}/>;
			// Votes on the feature
			appendVotes(result, f);
			
			// Attributes of the entity
			var attrs: XML = <attrs/>;
			for each (var a: Object in f.attrs) {
				appendAttr(attrs, a);
			}
			result.appendChild(attrs);
			
			// Comments of the feature
			var cs: XML = <comments/>;
			for each (var c: Object in f.comments) {
				cs.appendChild(<comment creator={c.cid} ctime={c.ctime}>{c.content}</comment>);
			}
			result.appendChild(cs);
			
			return result;
		}
		
		private function createXmlFromBinary(b: Object): XML {
			var result: XML = 
				<binary id={b.id} creator={b.cid} ctime={b.ctime} mid={b.mid} mtime={b.mtime}
					typeId={b.typeId} sourceId={b.source} targetId={b.target}/>;
			appendVotes(result, b);
			return result;
		}
		
		private function createXmlFromEntype(et: Object): XML {
			var result: XML = 
				<entype id={et.id} creator={et.cid} ctime={et.ctime} mid={et.mid} mtime={et.mtime}
					name={et.typeName} superId={et.superId} />;
			
			// append the attribute defs.
			var ad: XML = <attrDefs/>; 
			for each (var def: Object in et.attrDefs) {
				var a: XML = <attrDef id={def.id} creator={def.cid} ctime={def.ctime} mid={def.mid} mtime={def.mtime}
					name={def.name} type={def.type} multi={def.multi} dup={def.dup} />;
				if (Cst.ATTR_TYPE_ENUM == String(def.type)) {
					var enums: XML = <enums/>;
					for each (var enum: Object in def.enums) {
						enums.appendChild(<enum>{String(enum)}</enum>);
					}
					a.appendChild(enums);
				} else if (Cst.ATTR_TYPE_NUMBER == String(def.type)) {
					a.appendChild(<min>{def.min}</min>);
					a.appendChild(<max>{def.max}</max>);
					a.appendChild(<unit>{def.unit}</unit>);
				}
				ad.appendChild(a);
			}
			
			result.appendChild(ad);
			return result;
		}
		
		private function createXmlFromBintype(bt: Object): XML {
			var result: XML = 
				<bintype id={bt.id} creator={bt.cid} ctime={bt.ctime} mid={bt.mid} mtime={bt.mtime}
					name={bt.typeName} hier={bt.hier} dir={bt.dir}
					sourceTypeId={bt.sourceId} targetTypeId={bt.targetId} />;
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
		
		private function appendAttr(root: XML, obj: Object): void {
			var attr: XML = <attr id={obj.attrId} />;
			
			// Append values of the attributes 
			var vs: XML = <values/>;
			for each (var val: Object in obj.vals) {
				var xmlVal: XML = <value id={val.id} creator={val.cid} ctime={val.ctime} mid={val.mid} mtime={val.mtime}>
						<str>{val.val}</str>
					</value>;
				appendVotes(xmlVal, val);
				vs.appendChild(xmlVal);
			}
			attr.appendChild(vs);
			
			root.appendChild(attr);
		}
		
		public function stats(): String {
			// Number of features
			var numFeature: int = this.entities.source.length();
			// Number of common features (NO vote == 0)
			var numCommon: int = 0;
			// Number of optional features (NO vote == 1, 2, 3, ...)
			var numOpt: Array = [];
			
			for each (var f: Object in this.entities.source) {
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
	}
}