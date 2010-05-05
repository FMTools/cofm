package collab.fm.client.event {
	import flash.events.Event;

	public class ClientEvent extends Event {

		public static const CONNECT_SUCCESS: String = "connectSuccess";
		public static const REGISTER_SUCCESS: String = "registerSuccess";
		public static const CURRENT_FEATURE_DELETED: String = "currentFeatureDeleted";
		public static const BASIC_INFO_UPDATED: String = "basicInfoUpdated";
		public static const BACK_TO_HOME: String = "backToHome";

		public var data: Object;

		public function ClientEvent(type:String, data:Object=null, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.data = data;
		}

		override public function clone(): Event {
			return new ClientEvent(type, data, bubbles, cancelable);
		}
	}
}