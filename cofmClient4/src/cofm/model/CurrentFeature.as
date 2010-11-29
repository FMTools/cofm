package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.collections.IViewCursor;
	import mx.collections.Sort;
	import mx.collections.SortField;
	import mx.collections.XMLListCollection;
	
	/**
	 * Only store the basic info and relatioinships.
	 */
	public class CurrentFeature implements IOperationListener {
		private static var _instance: CurrentFeature = new CurrentFeature();
		
		public var element: XML;
		
		public var id: int;
		
		[Bindable]
		public var votes: ArrayCollection = new ArrayCollection();
		
		[Bindable]
		public var parents: ArrayCollection = new ArrayCollection();
		
		[Bindable]
		public var children: ArrayCollection = new ArrayCollection();
		
		[Bindable]
		public var binaryConstraints: ArrayCollection = new ArrayCollection();
		
		[Bindable]
		public var basicInfo: XMLListCollection = new XMLListCollection();
		
		[Bindable] public var typeId: int;
		[Bindable] public var name: String;
		[Bindable] public var kind: String;
		
		public static function instance(): CurrentFeature {
			return _instance;
		}
		
		public function CurrentFeature() {
			Model.instance().registerSubView(this);
			
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.SUCCESS, onModelUpdate);
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.FEATURE_SELECTED, onCurrentFeatureSelected);
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.TYPE_SELECTED, onTypeSelected);
			
			id = -1;
		}
		
		private function toUserArray(list: XMLList): Array {
			var rslt: Array = new Array();
			for each (var u: Object in list) {
				rslt.push(u.text().toString());
			}
			return rslt;
		}
		
		private function clear(): void {
			id = -1;
			typeId = -1;
			name = "";
			votes.source = [];
			parents.source = [];
			children.source = [];
			binaryConstraints.source = [];
			basicInfo.source = null;
			
		}
		
		private function onModelUpdate(evt: ModelUpdateEvent): void {
			// clear current feature
			this.clear();
		}
		
		private function onTypeSelected(evt: FeatureSelectEvent): void {
			this.clear();
			
			kind = RefinementTreeData.KIND_CLASS;
			id = evt.id;
			typeId = id;
			name = evt.name;
			element = XML(Model.instance().entypes.source.(@id==String(evt.id))[0]);
			
			// No updating on binding data.
		}
		
		private function onCurrentFeatureSelected(evt: FeatureSelectEvent): void {	
			this.clear();
			
			kind = RefinementTreeData.KIND_OBJECT;
			id = evt.id;
			name = evt.name;
			element = XML(Model.instance().entities.source.(@id==String(evt.id))[0]);
			typeId = element.@typeId;
			
			// update creator info
			var creator: String = UserList.instance().getNameById(int(element.@creator));
			basicInfo.addItem(<attr name="creator" type={Cst.ATTR_TYPE_STRING} label="Creator" value={creator}/>);
			
			updateVotes(); // votes to this feature
			updateRefinements();
			updateBinaryConstraints();
			updateAllAttrbutes();
			
			// update basic info
			ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			trace("CurrentFeature - basic info updated.");
			
		}
		
		private function updateVotes(): void {
			votes.source = [];
			// no votes
			var noNum: int = XMLList(element.no.user).length();
			var yesNum: int = XMLList(element.yes.user).length();
			var yesRatio: Number = (noNum <= 0) ? 100 : (100 * yesNum / (yesNum + noNum));
			var noRatio: Number = 100 - yesRatio;
			
			votes.addItem({
				"label": RS.m_fe_basic_votes_no,
				"num": noNum,
				"ratio": ((noRatio > 0) ? noRatio.toPrecision(3) : "0") + "%",
				"n": toUserArray(XMLList(element.no.user))
			});
			// yes votes
			votes.addItem({
				"label": RS.m_fe_basic_votes_yes,
				"num": yesNum,
				"ratio": ((yesRatio > 0) ? yesRatio.toPrecision(3) : "0") + "%",
				"y": toUserArray(XMLList(element.yes.user))
			});
		}
		
		private function updateAllAttrbutes(): void {
			for each (var attr: Object in element.attrs.attr) {
				updateAttr(XML(attr));
			}
		}
		
		private function updateAttr(a: XML): void {
			var def: XML = Model.instance().getAttrDefById(element, String(a.@id));
			
			var result: XML = <attr name={def.@name} type={def.@type} />;
			
			// result.@label = "{Name}:" (Capitalize the first letter of a.@name)
			var label: String = def.@name;
			label = label.charAt(0).toUpperCase() + label.substr(1);
			result.@label = label;
			
			if (String(def.@type) != Cst.ATTR_TYPE_ENUM) {
				// result.@value = the primary value of a.values
				var val: Object = ModelUtil.getPrimaryValueAndRate(a.values.value);
				if (val.value == null) {
					return;
				}
				result.@value = val.value;
				result.@rate = val.rate;
				if (String(def.@type) == Cst.ATTR_TYPE_NUMBER) {
					result.@unit = a.unit.text().toString();
				}
			} else {
				for each (var e: Object in def.enums.enum) {
					var en: XML = <enum value={XML(e).text().toString()} />;
					en.@rate = ModelUtil.getSupportRateOfValue(en.@value, a.values.value);
					result.appendChild(en);
				}
			}
			
			// replace the attribute with the same name in basicInfo
			for (var cursor: IViewCursor = basicInfo.createCursor(); !cursor.afterLast; cursor.moveNext()) {
				if (cursor.current.@name == result.@name) {
					cursor.remove();
					cursor.insert(result);
					return;
				}
			}
			// ... or create a new attribute
			basicInfo.addItem(result);
		}
		
		
		private function updateRefinements(): void {
			parents.source = [];
			children.source = [];
			for each (var r: Object in Model.instance().binaries.source) {
				if (Model.instance().isInstanceOfRefinement(XML(r))) {
					if (r.@sourceId == String(this.id)) {
						children.addItem({
							"id": r.@id,
							"name": ModelUtil.getFeatureNameById(r.@targetId),
							"numSupporters": XMLList(r.yes.user).length(),
							"numOpponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"typeId": r.@typeId,
							"left": r.@sourceId,
							"right": r.@targetId
						});
					} else if (r.@targetId == String(this.id)) {
						parents.addItem({
							"id": r.@id,
							"name": ModelUtil.getFeatureNameById(r.@sourceId),
							"numSupporters": XMLList(r.yes.user).length(),
							"numOpponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"typeId": r.@typeId,
							"left": r.@sourceId,
							"right": r.@targetId
						});
					}
				}
			}
		}
		
		private function updateBinaryConstraints(): void {
			binaryConstraints.source = [];
			for each (var r: Object in Model.instance().binaries.source) {
				if (!Model.instance().isInstanceOfRefinement(XML(r))) {
					var tp: XML = Model.instance().getBinRelationTypeByInstance(XML(r));
					if (tp == null) {
						continue;
					}
					if (r.@sourceId == String(this.id)) {
						binaryConstraints.addItem({
							"id": r.@id,
							"name": "this <b><font color='#088A08'>" 
									+ tp.@name + "</font></b> " 
									+ ModelUtil.getFeatureNameById(r.@targetId),
							"numSupporters": XMLList(r.yes.user).length(),
							"numOpponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"typeId": r.@typeId,
							"left": r.@sourceId,
							"right": r.@targetId
						});
					} else if (r.@targetId == String(this.id)) {
						binaryConstraints.addItem({
							"id": r.@id,
							"name": ModelUtil.getFeatureNameById(r.@sourceId) 
									+ " <b><font color='#088A08'>" 
									+ tp.@name + "</font></b> this",
							"numSupporters": XMLList(r.yes.user).length(),
							"numOpponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"typeId": r.@typeId,
							"left": r.@sourceId,
							"right": r.@targetId
						});
					}
				}
			}
			
			// Sort binaryConstraints on "type" first, "id" second.
			var sort: Sort = new Sort();
			sort.fields = [new SortField("typeId", true), new SortField("id", true)];
			binaryConstraints.sort = sort;
			binaryConstraints.refresh();
		}
		
		public function handleEditAddEntityType(op: Object): void {
			// do nothing
		}
		
		public function handleEditAddBinRelType(op: Object): void {
			// do nothing
		}
		
		public function handleVoteAddEntity(op:Object): void {
			if (op["entityId"] == String(id)) {
				if (op[Model.SHOULD_DELETE_ELEMENT] == true) {
					this.clear();
				} else {
					this.updateVotes();
				}
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}
		
		public function handleVoteAddBinRel(op:Object): void {
			if (op["sourceId"] == String(id) || op["targetId"] == String(id)) {
				if (Model.instance().isInstanceOfRefinementByTypeId(op["typeId"])) {
					this.updateRefinements();
				} else {
					this.updateBinaryConstraints();
				}
			}
		}
		
		// Do nothing with add attribute methods
		public function handleEditAddAttributeDef(op: Object): void {
			
		}
		
		public function handleEditAddEnumAttributeDef(op: Object): void {
			
		}
		
		public function handleEditAddNumericAttributeDef(op: Object): void {
			
		}
		
		public function handleVoteAddValue(op: Object): void {
			if (op["entityId"] == String(id)) {
				var ent: XML = Model.instance().getEntityById(op["entityId"]);
				if (ent == null) {
					return;
				}
				var a: XML = Model.instance().getAttrById(ent, op["attrId"]);
				this.updateAttr(a);
				
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}
		
		public function handleInferVoteOnEntity(op:Object): void {
			if (op["entityId"] == String(id)) {
				this.updateVotes();
				
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}
		
		public function handleInferVoteOnRelation(op:Object): void {
			updateRefinements();
			updateBinaryConstraints();
			
			// update basic info
			//ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
		}
	}
}