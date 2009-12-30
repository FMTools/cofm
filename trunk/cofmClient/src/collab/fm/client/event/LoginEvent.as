package collab.fm.client.event {
	import flash.events.Event;

	public class LoginEvent extends Event {

		public static const SUCCESS: String = "loginSuccess";

		public var myId: int;

		public function LoginEvent(type: String, myId: int, bubbles: Boolean = true, cancelable: Boolean = false) {
			super(type, bubbles, cancelable);
			this.myId = myId;
		}

		override public function clone(): Event {
			return new LoginEvent(type, myId, bubbles, cancelable);
		}

	}
}