package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.collections.XMLListCollection;
	import mx.controls.Alert;

	public class PersonalTreeData extends RefinementTreeData
	{
		
		public var pvId: int;
		
		
		public function PersonalTreeData()
		{
			this.xml = new XMLListCollection(new XMLList(<node/>));
			this.pvId = -1;
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.OTHER_PEOPLE_SELECT_ON_TREE, onOtherPeopleSelect);
			ClientEvtDispatcher.instance().addEventListener(
				LogoutEvent.LOGGED_OUT, onPeopleLogout);
			ClientEvtDispatcher.instance().addEventListener(
				PageSwitchEvent.OTHERS_EXIT_WORK_PAGE, onPeopleExitModel);
			ClientEvtDispatcher.instance().addEventListener(
				PersonalViewUpdateEvent.SUCCESS, onPersonalViewUpdated);
		}
		
		public function resetToEmpty(): void {
			this.refinements = new Dictionary();
			
			this.xml = new XMLListCollection(new XMLList(<node/>));
			
			// Create the Class Nodes
			for each (var c: Object in Model.instance().entypes.source) {
				this.createRootClassNode(XML(c));
			}
		}
		
		private function onPersonalViewUpdated(evt: PersonalViewUpdateEvent): void {
			if (this.pvId >= 0 && this.pvId != int(evt.pv["pvId"])) {
				return;  // Not this personal view
			}
			
			if (this.pvId < 0) {
				this.pvId = int(evt.pv["pvId"]);
			}
			
			beforeDataUpdating();
			
			resetToEmpty();
			
			// Create the Object Nodes in this Personal View
			for each (var enId: Object in (evt.pv["entities"] as Array)) {
				var f: XML = Model.instance().getEntityById(String(enId));
				if (f != null) {
					var node: XML = this.createEntity(f.@id);
					if (node != null) {
						addToRootOfClass(node);
					}
				}
			}
			
			// Create Refinements between Objects in this Personal View
			for each (var rId: Object in (evt.pv["binrels"] as Array)) {
				var r: XML = Model.instance().getBinRelationById(String(rId));
				if (r != null && Model.instance().isInstanceOfRefinement(XML(r))) {
					addRefinement(r.@id);
				}
			}
			
			afterDataUpdated();
		}
		
		override protected function getEntityDisplayName(feature: Object): String {
			var allNames: XMLList = Model.instance().getValuesByAttrName(XML(feature), Cst.ATTR_FEATURE_NAME);
			if (allNames.length() <= 0) {
				return UNNAMED;
			}
			
			// Build an array from the XML.
			var ns: Array = [];
			var onlyOneName: Boolean = allNames.length() == 1;
			for each (var n: Object in allNames) {
				if (onlyOneName) {
					if (XMLList(n.no.user.(text().toString()==String(UserList.instance().myId))).length() > 0) {
						return UNNAMED;
					} else {
						return XML(n.str).text().toString();
					}
				}
				var nameInTree: Boolean = false;
				var yes: Array = [];
				for each (var u: Object in n.yes.user) {
					if (UserList.instance().myId == int(u)) {
						yes.push(u);
						nameInTree = true;
					}
				}
				var no: Array = [];
				for each (var u1: Object in n.no.user) {
					no.push(u1);
				}
				if (nameInTree) {
					ns.push({
						val: XML(n.str).text().toString(),
						v1: yes,
						v0: no
					});
				}
			}
			if (ns.length == 0) {
				return UNNAMED;
			}
			ModelUtil.sortOnRating(ns, "v1", "v0", UserList.instance().myId);
			return ns[0].val;
		}
		
		override protected function isPartOfTree(o: Object): Boolean {
			//return true is I am a "YES" voter.
			var me: String = String(UserList.instance().myId);
			return XMLList(o.yes.user.(text().toString()==me)).length() > 0;
		}
		
		override protected function afterDataUpdated(): void {
		}
		
		override protected function beforeDataUpdating(): void {
		}
		
		override public function stats(): String {
			return "PersonalTree - " + super.stats();
		}
	}
}