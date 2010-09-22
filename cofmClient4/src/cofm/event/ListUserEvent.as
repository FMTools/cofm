package cofm.event 
{	
	import flash.events.Event;
	import flash.utils.Dictionary;

	public class ListUserEvent extends Event {
		public static const SUCCESS: String = "listUserSuccess";
		public static const LOCAL_COMPLETE: String = "localComplete";
		public static const APPEND: String = "appendNewUser";

		public var users: Dictionary;

		public function ListUserEvent(type:String, users: Dictionary, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.users = users;
		}

		override public function clone(): Event {
			return new ListUserEvent(type, users, bubbles, cancelable);
		}

	}
}