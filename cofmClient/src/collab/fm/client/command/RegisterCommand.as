package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class RegisterCommand implements IDurableCommand {

		private var _name:String;
		private var _pwd: String;
		private var _cmdId: int;

		public function RegisterCommand(name: String, pwd: String) {
			_name = name;
			_pwd = pwd;
		}

		/** Register format see Server.RegisterRequest
		 */
		public function execute(): void {
			_cmdId = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					"id": _cmdId,
					"name": Cst.REQ_REGISTER,
					"user": _name,
					"pwd": _pwd
				};
			Connector.instance.send(JsonUtil.objectToJson(request));
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
			throw new Error("Unsupported Operation Error.");
		}

		/** Response format: (see server.ListModelResponse for details)
		 *      models: array of models.
		 */
		public function handleResponse(data:Object): void {
			trace("Register reponse received: " + data[Cst.FIELD_RSP_NAME]);
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]
				&& Cst.REQ_REGISTER == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_cmdId);
				ClientEvtDispatcher.instance().dispatchEvent(
					new ClientEvent(ClientEvent.REGISTER_SUCCESS));
			}
		}

	}
}