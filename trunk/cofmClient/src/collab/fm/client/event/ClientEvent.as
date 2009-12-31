package collab.fm.client.event {
	import flash.events.Event;

	public class ClientEvent extends Event {

		public static const CONNECT_SUCCESS: String = "connectSuccess";

		public static const REGISTER_SUCCESS: String = "registerSuccess";

		public static const LIST_MODEL_SUCCESS: String = "listModelSuccess";
		public static const SEARCH_MODEL_SUCCESS: String = "searchModelSuccess";

		public static const LOGIN_SUCCESS: String = "loginSuccess";

		public function ClientEvent(type:String, bubbles:Boolean=true, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
		}

		override public function clone(): Event {
			return new ClientEvent(type, bubbles, cancelable);
		}
	}
}