package cofm.command 
{
	import cofm.event.*;
	import cofm.model.*;
	import cofm.util.*;
	
	import com.adobe.crypto.MD5;

	public class LoginCommand extends AbstractDurableCommand {
		private var _name: String;
		private var _pwd: String;

		public function LoginCommand(name: String, pwd: String) {
			super();
			_name = name;
			_pwd = MD5.hash(pwd);
		}

		override protected function createRequest():Object {
			return {
					"name": Cst.REQ_LOGIN,
					"user": _name,
					"pwd": _pwd
				};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_LOGIN == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				var _id: int = int(data[Cst.FIELD_RSP_SOURCE_USER_ID]);
				ClientEvtDispatcher.instance().dispatchEvent(
					new LoginEvent(LoginEvent.SUCCESS, _id, _name));
			}
		}
		
	}
}