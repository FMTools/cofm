package cofm.event 
{
	import flash.events.Event;

	public class MsgEvent extends Event {
		public static const INFO: String = "info";
		public static const WARN: String = "warn";
		public static const ERROR: String = "error";

		public var msg: String;

		public function MsgEvent(type:String, msg: String, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.msg = msg;
		}

		override public function clone(): Event {
			return new MsgEvent(type, msg, bubbles, cancelable);
		}
	}
}