package collab.fm.client.event {
	import flash.events.Event;

	public class FeatureSelectEvent extends Event {
		public static const SELECT_FROM_TREE: String = "SelectFromTree";

		public var id: int;
		public var name: String;

		public function FeatureSelectEvent(type:String, id: int, name: String, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.id = id;
			this.name = name;
		}

		override public function clone(): Event {
			return new FeatureSelectEvent(type, id, name, bubbles, cancelable);
		}

	}
}