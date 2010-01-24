package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.User;
	import collab.fm.client.util.*;

	import flash.events.IEventDispatcher;

	public class DatalessCommand implements IDurableCommand {
		protected var _target: IEventDispatcher;
		protected var _name: String;
		protected var _cmdId: int;
		private var _needMyId: Boolean;
		private var _needModelId: Boolean;

		public function DatalessCommand(target: IEventDispatcher, name: String, needMyId: Boolean=true, needModelId: Boolean=true) {
			_target = target;
			_name = name;
			_needMyId = needMyId;
			_needModelId = needModelId;
		}

		public function execute(): void {
			_cmdId = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					"id": _cmdId,
					"name": _name
				};
			if (_needMyId) {
				request.requesterId = User.instance.myId;
			}
			if (_needModelId) {
				request.modelId = User.instance.currentModelId;
			}
			Connector.instance.send(JsonUtil.objectToJson(request));
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
			throw new Error("Unsupported Operation Error.");
		}

		public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]
				&& _name == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_cmdId);

				refreshDataAndViews(data);
			}
		}

		protected function refreshDataAndViews(data: Object): void {
		}

	}
}