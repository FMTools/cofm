package cofm.util
{
	import cofm.event.*;
	/**
	 * The API for showing messages for users.
	 * The MsgEvent dispatched by this class is listened by the MessageBar component.
	 */  
	public class Msg {
		public static function info(m: String): void {
			ClientEvtDispatcher.instance().dispatchEvent(new MsgEvent(MsgEvent.INFO, m));
		}
		
		public static function warn(m: String): void {
			ClientEvtDispatcher.instance().dispatchEvent(new MsgEvent(MsgEvent.WARN, m));
		}
		
		public static function error(m: String): void {
			ClientEvtDispatcher.instance().dispatchEvent(new MsgEvent(MsgEvent.ERROR, m));
		}
		
		public static function showResponse(type: String, m: String): void {
			switch (type) {
				case Cst.RSP_SUCCESS:
					info(m);
					break;
				case Cst.RSP_ERROR:
				case Cst.RSP_SERVER_ERROR:
					error(m);
					break;
				case Cst.RSP_STALE:
					warn(m);
					break;
			}
		}
	}
}