package cofm.command 
{
	import cofm.event.*;
	import cofm.model.*;
	import cofm.util.*;
	
	import com.adobe.crypto.MD5;
	
	import flash.utils.Dictionary;
	
	import mx.controls.Alert;

	public class RegisterCommand implements IDurableCommand {

		private var _name:String;
		private var _pwd: String;
		private var _mail: String;
		private var _cmdId: int;

		public function RegisterCommand(name: String, pwd: String, mail: String) {
			_name = name;
			_pwd = MD5.hash(pwd);
			_mail = mail;
		}

		/** Register format see Server.RegisterRequest
		 */
		public function execute(): void {
			_cmdId = CommandBuffer.instance().addCommand(this);
			var request: Object = {
					"id": _cmdId,
					"name": Cst.REQ_REGISTER,
					"user": _name,
					"pwd": _pwd,
					"mail": _mail
				};
			Connector.instance().send(request);
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
			if (Cst.REQ_REGISTER == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]) {
	
					CommandBuffer.instance().removeCommand(_cmdId);
					ClientEvtDispatcher.instance().dispatchEvent(
						new ClientEvent(ClientEvent.REGISTER_SUCCESS));
					
					Alert.show("A validation mail has been sent to your mail box.", "Registration Succeed");
					
					var d: Dictionary = new Dictionary();
					var key: String = String(data[Cst.FIELD_RSP_SOURCE_USER_ID]);
					d[key] = _name;
					ClientEvtDispatcher.instance().dispatchEvent(
						new ListUserEvent(ListUserEvent.APPEND, d));
				} else if (Cst.RSP_ERROR == data[Cst.FIELD_RSP_NAME]) {
					CommandBuffer.instance().removeCommand(_cmdId);
					ClientEvtDispatcher.instance().dispatchEvent(
						new ClientEvent(ClientEvent.REGISTER_FAILED));
				}
			}
		}

	}
}