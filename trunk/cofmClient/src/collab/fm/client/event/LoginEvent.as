package collab.fm.client.event {
	import flash.events.Event;

	public class LoginEvent extends Event {
		public static const SUCCESS: String = "loginSuccess";
		public static const LOGOUT: String = "loggedOut";

		public var myId: int;
		public var myName: String;

		public function LoginEvent(type:String, myId: int, myName: String, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.myId = myId;
			this.myName = myName;
		}

		override public function clone(): Event {
			return new LoginEvent(type, myId, myName, bubbles, cancelable);
		}

	}
}