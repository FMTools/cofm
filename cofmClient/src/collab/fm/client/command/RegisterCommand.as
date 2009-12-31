package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.event.ClientEvent;
	import collab.fm.client.util.*;

	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;

	public class RegisterCommand implements IDurableCommand {

		private var _name:String;
		private var _pwd: String;
		private var _cmdId: int;
		private var _target: IEventDispatcher;

		public function RegisterCommand(target: IEventDispatcher, name: String, pwd: String) {
			_target = target;
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
				// Notify views
				_target.dispatchEvent(new ClientEvent(ClientEvent.REGISTER_SUCCESS));

				CommandBuffer.instance.removeCommand(_cmdId);
			}
		}

	}
}