package collab.fm.client.event {
	import flash.events.Event;

	public class FeatureSelectEvent extends Event {
		public static const CLICK_ON_TREE: String = "ClickOnTree";
		public static const DB_CLICK_ON_TREE: String = "DoubleClickOnTree";
		public static const OTHER_PEOPLE_SELECT_ON_TREE: String = "OtherPeopleSelectOnTree";

		public var id: int;
		public var name: String;
		public var model: int;
		public var user: int;

		public function FeatureSelectEvent(type:String, id: int, name: String, model: int=-1, user: int=-1, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.id = id;
			this.name = name;
			this.model = model;
			this.user = user;
		}

		override public function clone(): Event {
			return new FeatureSelectEvent(type, id, name, model, user, bubbles, cancelable);
		}

	}
}