package cofm.component.home
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class ModelCollectionUpdater
	{
		
		private static var _instance: ModelCollectionUpdater = new ModelCollectionUpdater();
		
		public static const FAST_MODE: int = 1;
		public static const SAFE_MODE: int = 2;
		
		private var isIdle: Boolean;
		private var searchStr: String;
		public static function instance(): ModelCollectionUpdater {
			return _instance;
		}
		
		public function ModelCollectionUpdater()
		{
			isIdle = true;	
			ClientEvtDispatcher.instance().addEventListener(ListUserEvent.LOCAL_COMPLETE, onListUser);
		}
		
		public function update(mode: int = SAFE_MODE, searchStr: String = null): void {
			isIdle = false;
			this.searchStr = searchStr;
			if (mode == SAFE_MODE) {
				new ListUserCommand().execute();
			} else if (mode == FAST_MODE) {
				new ListModelCommand(searchStr).execute();
				isIdle = true;
			}
		}
		
		private function onListUser(evt: ListUserEvent): void {
			if (!isIdle) { // if this event is caused by the update() method
				isIdle = true;
				new ListModelCommand(this.searchStr).execute();
			}
		}

	}
}