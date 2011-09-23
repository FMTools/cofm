package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	import flash.events.IEventDispatcher;

	public class DatalessCommand extends AbstractDurableCommand {
		protected var _name: String;
		private var _needMyId: Boolean;
		private var _needModelId: Boolean;

		public function DatalessCommand(name: String, needMyId: Boolean=true, needModelId: Boolean=true) {
			super();
			_name = name;
			_needMyId = needMyId;
			_needModelId = needModelId;
		}

		override protected function createRequest():Object {
			var request: Object = {
				"name": _name
			};
			if (_needMyId) {
				request.requesterId = UserList.instance().myId;
			}
			if (_needModelId) {
				request.modelId = ModelCollection.instance().currentModelId;
			}
			return request;
		}
		
		override protected function handleSuccess(data:Object):void {
			if (_name == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				refreshDataAndViews(data);
			}
		}

		protected function refreshDataAndViews(data: Object): void {
		}

	}
}