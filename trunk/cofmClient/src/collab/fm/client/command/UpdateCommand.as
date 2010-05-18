package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class UpdateCommand implements IDurableCommand {
		private var _cmdId: int;
		private var _modelId: int;

		public function UpdateCommand(modelId: int) {
			_modelId = modelId;
		}

		public function execute(): void {
			_cmdId = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					"id": _cmdId,
					"name": Cst.REQ_UPDATE,
					"requesterId": UserList.instance.myId,
					"modelId": _modelId
				};
			Connector.instance.send(JsonUtil.objectToJson(request));
			Console.info("UpdateCommand - send requrest (modelId = " + _modelId + ")");
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
		}

		public function handleResponse(data:Object): void {
			Console.info("UpdateCommand - recv response (" + data[Cst.FIELD_RSP_NAME] + ")");
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]
				&& Cst.REQ_UPDATE == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_cmdId);

				var theModel: Object = {
						"features": data["features"],
						"binaries": data["binaries"]
					};
				Console.info("UpdateCommand - dispatch ModelUpdateEvent.SUCCESS");
				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelUpdateEvent(ModelUpdateEvent.SUCCESS, theModel));
			}
		}
	}
}