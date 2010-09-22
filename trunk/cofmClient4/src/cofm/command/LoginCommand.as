package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class LoginCommand implements IDurableCommand {
		private var _name: String;
		private var _pwd: String;
		private var _cmdId: int;

		public function LoginCommand(name: String, pwd: String) {
			_name = name;
			_pwd = pwd;
		}

		/** Request format (see server.LoginRequest)
		 */
		public function execute(): void {
			var cmdId: int = CommandBuffer.instance.addCommand(this);
			_cmdId = cmdId;
			// Build a login request object and convert it to json
			var req: Object = {
					"id": cmdId,
					"name": Cst.REQ_LOGIN,
					"user": _name,
					"pwd": _pwd
				};
			Connector.instance.send(JsonUtil.objectToJson(req));
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val: Boolean): void {
			// ignored, must be durable
			throw new Error("Unsupported Operation Exception");
		}

		/** Response format: (see server.Response)
		 */
		public function handleResponse(data: Object): void {
			trace("Login response received as: " + data);
			if (Cst.RSP_SUCCESS == (data[Cst.FIELD_RSP_NAME] as String)
				&& Cst.REQ_LOGIN == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				var _id: int = int(data[Cst.FIELD_RSP_SOURCE_USER_ID]);
				CommandBuffer.instance.removeCommand(_cmdId);
				ClientEvtDispatcher.instance().dispatchEvent(
					new LoginEvent(LoginEvent.SUCCESS, _id, _name));
			}
		}

	}
}