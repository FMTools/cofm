package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
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
		
		private var _feature: XML;
		
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
		
		public static function instance(): CurrentFeature {
			return _instance;
		}
		
		public function CurrentFeature() {
			Model.instance().registerSubView(this);
			
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.SUCCESS, onModelUpdate);
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.FEATURE_SELECTED, onCurrentFeatureSelected);
			
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
		
		private function onCurrentFeatureSelected(evt: FeatureSelectEvent): void {	
			this.clear();
			
			// Set the feature id
			id = evt.id;
			_feature = XML(Model.instance().entities.source.(@id==String(evt.id))[0]);
			
			// update creator info
			var creator: String = UserList.instance().getNameById(int(_feature.@creator));
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
			var noNum: int = XMLList(_feature.no.user).length();
			var yesNum: int = XMLList(_feature.yes.user).length();
			var yesRatio: Number = (noNum <= 0) ? 100 : (100 * yesNum / (yesNum + noNum));
			var noRatio: Number = 100 - yesRatio;
			
			votes.addItem({
				"label": RS.m_fe_basic_votes_no,
				"num": noNum,
				"ratio": ((noRatio > 0) ? noRatio.toPrecision(3) : "0") + "%",
				"n": toUserArray(XMLList(_feature.no.user))
			});
			// yes votes
			votes.addItem({
				"label": RS.m_fe_basic_votes_yes,
				"num": yesNum,
				"ratio": ((yesRatio > 0) ? yesRatio.toPrecision(3) : "0") + "%",
				"y": toUserArray(XMLList(_feature.yes.user))
			});
		}
		
		private function updateAllAttrbutes(): void {
			for each (var attr: Object in _feature.attrs.attr) {
				updateAttr(XML(attr));
			}
		}
		
		private function updateAttr(a: XML): void {
			
			var result: XML = <attr name={a.@name} type={a.@type} />;
			
			// result.@label = "{Name}:" (Capitalize the first letter of a.@name)
			var label: String = a.@name;
			label = label.charAt(0).toUpperCase() + label.substr(1);
			result.@label = label;
			
			if (String(a.@type) != Cst.ATTR_TYPE_ENUM) {
				// result.@value = the primary value of a.values
				var val: Object = ModelUtil.getPrimaryValueAndRate(a.values.value);
				if (val.value == null) {
					return;
				}
				result.@value = val.value;
				result.@rate = val.rate;
				if (String(a.@type) == Cst.ATTR_TYPE_NUMBER) {
					result.@unit = a.unit.text().toString();
				}
			} else {
				for each (var e: Object in a.enums.enum) {
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
				if (r.@type == Cst.BIN_REL_REFINES) {
					if (r.@left == String(this.id)) {
						children.addItem({
							"id": r.@id,
							"name": ModelUtil.getFeatureNameById(r.@right),
							"supporters": XMLList(r.yes.user).length(),
							"opponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"type": Cst.BIN_REL_REFINES,
							"left": r.@left,
							"right": r.@right
						});
					} else if (r.@right == String(this.id)) {
						parents.addItem({
							"id": r.@id,
							"name": ModelUtil.getFeatureNameById(r.@left),
							"supporters": XMLList(r.yes.user).length(),
							"opponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"type": Cst.BIN_REL_REFINES,
							"left": r.@left,
							"right": r.@right
						});
					}
				}
			}
		}
		
		private function updateBinaryConstraints(): void {
			binaryConstraints.source = [];
			for each (var r: Object in Model.instance().binaries.source) {
				if (r.@type == Cst.BIN_REL_REQUIRES) {
					if (r.@left == String(this.id)) {
						binaryConstraints.addItem({
							"id": r.@id,
							"name": "this requires " + ModelUtil.getFeatureNameById(r.@right),
							"supporters": XMLList(r.yes.user).length(),
							"opponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"type": Cst.BIN_REL_REQUIRES,
							"left": r.@left,
							"right": r.@right
						});
					} else if (r.@right == String(this.id)) {
						binaryConstraints.addItem({
							"id": r.@id,
							"name": ModelUtil.getFeatureNameById(r.@left) + " requires this",
							"supporters": XMLList(r.yes.user).length(),
							"opponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"type": Cst.BIN_REL_REQUIRES,
							"left": r.@left,
							"right": r.@right
						});
					}
				} else if (r.@type == Cst.BIN_REL_EXCLUDES) {
					if (r.@left == String(this.id)) {
						binaryConstraints.addItem({
							"id": r.@id,
							"name": "this excludes " + ModelUtil.getFeatureNameById(r.@right),
							"supporters": XMLList(r.yes.user).length(),
							"opponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"type": Cst.BIN_REL_EXCLUDES,
							"left": r.@left,
							"right": r.@right
						});
					} else if (r.@right == String(this.id)) {
						binaryConstraints.addItem({
							"id": r.@id,
							"name": "this excludes " + ModelUtil.getFeatureNameById(r.@left),
							"supporters": XMLList(r.yes.user).length(),
							"opponents": XMLList(r.no.user).length(),
							"y": toUserArray(XMLList(r.yes.user)),
							"n": toUserArray(XMLList(r.no.user)),
							"type": Cst.BIN_REL_EXCLUDES,
							"left": r.@left,
							"right": r.@right
						});
					}
				}
			}
			
			// Sort binaryConstraints on "type" first, "id" second.
			var sort: Sort = new Sort();
			sort.fields = [new SortField("type", true), new SortField("id", true)];
			binaryConstraints.sort = sort;
			binaryConstraints.refresh();
		}
		
		public function handleEditAddEntityType(op: Object): void {
			// TODO
		}
		
		public function handleEditAddBinRelType(op: Object): void {
			// TODO
		}
		
		public function handleVoteAddEntity(op:Object): void {
			if (op["featureId"] == String(id)) {
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
			if (op["leftFeatureId"] == String(id) || op["rightFeatureId"] == String(id)) {
				switch (op["type"]) {
					case Cst.BIN_REL_REFINES:
						this.updateRefinements();
						break;
					case Cst.BIN_REL_REQUIRES:
					case Cst.BIN_REL_EXCLUDES:
						this.updateBinaryConstraints();
						break;
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
			if (op["featureId"] == String(id)) {
				
				var a: XMLList = Model.instance()
					.entities.source.(@id==op["featureId"])    // Find the feature with specific ID...
					..attr.(@name==op["attr"]); // then find the specific attribute in this feature
				if (a.length() <= 0) {
					return; // No such attribute, return.
				}
				this.updateAttr(XML(a[0]));
				
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}
		
		public function handleInferVoteOnEntity(op:Object): void {
			if (op["featureId"] == String(id)) {
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