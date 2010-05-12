package collab.fm.client.util
{
	import collab.fm.client.command.*;
	import collab.fm.client.event.*;
	
	public class ModelListUpdater
	{
		
		private static var _instance: ModelListUpdater = new ModelListUpdater();
		
		public static const FAST_MODE: int = 1;
		public static const SAFE_MODE: int = 2;
		
		private var isIdle: Boolean;
		private var searchStr: String;
		public static function get instance(): ModelListUpdater {
			return _instance;
		}
		
		public function ModelListUpdater()
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