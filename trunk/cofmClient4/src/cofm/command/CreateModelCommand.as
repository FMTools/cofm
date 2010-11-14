package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	import mx.utils.StringUtil;
	public class CreateModelCommand implements IDurableCommand {
		private var _cmdId: int;
		private var _name: String;
		private var _des: String;

		public function CreateModelCommand(name: String, des: String) {
			_name = mx.utils.StringUtil.trim(name);
			_des = mx.utils.StringUtil.trim(des);
		}

		/** See server.CreateModelRequest
		 */
		public function execute(): void {
			_cmdId = CommandBuffer.instance().addCommand(this);
			var request: Object = {
					"name": Cst.REQ_CREATE_MODEL,
					"id": _cmdId,
					"requesterId": UserList.instance().myId,
					"modelName": _name,
					"description": _des
				};
			Connector.instance().send(JsonUtil.objectToJson(request));
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
		}

		public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME] &&
				Cst.REQ_CREATE_MODEL == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance().removeCommand(_cmdId);
				var theModel: XML = 
					<model isMine="true" id={data.modelId} name={_name} userNum="1">
						<des>{_des}</des>
						<users>
							<user>{UserList.instance().myId}</user>
						</users>
					</model>;
				// Model selection happens automatically after model creation.
				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelCreateEvent(ModelCreateEvent.SUCCESS, theModel));
			}
		}

	}
}