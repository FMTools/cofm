package cofm.model
{
	import cofm.command.ChangePersonalViewCommand;
	import cofm.event.*;
	import cofm.util.*;
	
	import flash.utils.Dictionary;
	
	import mx.collections.XMLListCollection;
	
	public class PersonalViewManager
	{
		private static var _instance: PersonalViewManager = new PersonalViewManager();
		
		private const _defaultXml: XML = <pvm><all/></pvm>;
		
		[Bindable] public var all: XMLListCollection;
		[Bindable] public var active: int;
		[Bindable] public var activeData: PersonalTreeData;
		
		private var pvData: Dictionary = new Dictionary();
		
		public static function instance(): PersonalViewManager {
			return _instance;
		}
		
		public function PersonalViewManager()
		{
			all = new XMLListCollection(new XMLList(_defaultXml.all));
			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.SUCCESS, onModelUpdated);
			ClientEvtDispatcher.instance().addEventListener(
				CreatePersonalViewEvent.SUCCESS, onPvCreated);
		}
		
		private function onPvCreated(evt: CreatePersonalViewEvent): void {
			var p: Object = evt.data;
			all.addItem(<pv id={p.pvId} creator={p.requesterId} ctime={p.execTime}
				mid={p.requesterId} mtime={p.execTime} name={p.pvName} />);
			all.refresh();
			
			changePV(int(p.pvId), false);
		}
		
		public function changePV(pvId: int, refreshData: Boolean): void {
			this.active = pvId;
			
			activeData = pvData[pvId];
			if (activeData == null) {
				activeData = new PersonalTreeData();
				activeData.pvId = pvId;
				pvData[pvId] = activeData;
				activeData.resetToEmpty();
			}
			
			var modelRegistry: Array = Model.instance().getSubViews();
			var oldActiveDataIndex: int = -1;
			for (var i: int = 0; i < modelRegistry.length; i++) {
				if (modelRegistry[i] instanceof PersonalTreeData) {
					oldActiveDataIndex = i; 
					break;
				}
			}
			// Remove old active data from model registry, and insert the new active data
			modelRegistry.splice(oldActiveDataIndex, (oldActiveDataIndex >= 0 ? 1 : 0), activeData);
			
			// Retrieve data from server (optional)
			if (refreshData) {
				new ChangePersonalViewCommand(ModelCollection.instance().currentModelId, pvId).execute();
			}
			
		}
		
		private function onModelUpdated(evt: ModelUpdateEvent): void {
			var root: XML = <all/>;
			
			for each (var p: Object in (evt.model.pvs as Array)) {
				root.appendChild(<pv id={p.id} creator={p.cid} ctime={p.ctime}
					mid={p.mid} mtime={p.mtime} name={p.name} />);
			}
			
			all.source = root.pv;
			
			all.filterFunction = 
				function (item: Object): Boolean {
					// only show my personal views
					return int(item.@creator) == UserList.instance().myId;  
				};
			all.refresh();
		}
	}
}