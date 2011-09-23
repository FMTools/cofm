package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class LogoutCommand extends AbstractDurableCommand {

		
		override protected function createRequest():Object {
			return {
				"name": Cst.REQ_LOGOUT,
					"requesterId": UserList.instance().myId
			};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_LOGOUT == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				ClientEvtDispatcher.instance().dispatchEvent(
					new LogoutEvent(LogoutEvent.LOGGED_OUT, UserList.instance().myId));
			}
		}

	}
}