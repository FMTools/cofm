package cofm.command 
{
	import cofm.event.*;
	import cofm.model.*;
	import cofm.util.*;
	
	import com.adobe.crypto.MD5;
	
	import flash.utils.Dictionary;
	
	import mx.controls.Alert;

	public class RegisterCommand extends AbstractDurableCommand {

		private var _name:String;
		private var _pwd: String;
		private var _mail: String;

		public function RegisterCommand(name: String, pwd: String, mail: String) {
			super();
			_name = name;
			_pwd = MD5.hash(pwd);
			_mail = mail;
		}
		
		override protected function createRequest():Object {
			return {
				"name": Cst.REQ_REGISTER,
					"user": _name,
					"pwd": _pwd,
					"mail": _mail
			};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_REGISTER == data[Cst.FIELD_RSP_SOURCE_NAME]) {
					ClientEvtDispatcher.instance().dispatchEvent(
						new ClientEvent(ClientEvent.REGISTER_SUCCESS));
					
					Alert.show("A validation mail has been sent to your mail box.", "Registration Succeed");
					
					var d: Dictionary = new Dictionary();
					var key: String = String(data[Cst.FIELD_RSP_SOURCE_USER_ID]);
					d[key] = _name;
					ClientEvtDispatcher.instance().dispatchEvent(
						new ListUserEvent(ListUserEvent.APPEND, d));
				} 
		}

		override protected function handleError(data: Object): void {
			if (Cst.REQ_REGISTER == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				ClientEvtDispatcher.instance().dispatchEvent(
							new ClientEvent(ClientEvent.REGISTER_FAILED));
			}
		}

	}
}