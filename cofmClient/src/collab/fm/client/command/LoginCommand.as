package collab.fm.client.command {

	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.ClientEvent;
	import collab.fm.client.util.*;

	import flash.events.IEventDispatcher;

	public class LoginCommand implements IDurableCommand {
		private var _id: int;
		private var _name: String;
		private var _pwd: String;
		private var _cmdId: int;
		private var _target: IEventDispatcher;

		public function LoginCommand(target: IEventDispatcher, name: String, pwd: String) {
			_target = target;
			this.name = name;
			this.pwd = pwd;
		}

		public function get id(): int {
			return _id;
		}

		public function set id(id: int): void {
			_id = id;
		}

		public function get name(): String {
			return _name;
		}

		public function set name(n: String): void {
			_name = n;
		}

		public function get pwd(): String {
			return _pwd;
		}

		public function set pwd(p: String): void {
			_pwd = p;
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
					"user": this.name,
					"pwd": this.pwd
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
				// Change data
				this.id = int(data[Cst.FIELD_RSP_SOURCE_USER_ID]);
				var changed: Object = {
						"event": Cst.DATA_MY_INFO,
						"myId": this.id,
						"myName": this.name
					}
				User.instance.refresh(changed, true);
				ModelCollection.instance.refresh(changed, true);

				// Notify the views
				_target.dispatchEvent(new ClientEvent(ClientEvent.LOGIN_SUCCESS));

				CommandBuffer.instance.removeCommand(_cmdId);
			}
		}

	}
}