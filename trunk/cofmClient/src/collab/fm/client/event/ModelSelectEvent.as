package collab.fm.client.event {
	import flash.events.Event;

	public class ModelSelectEvent extends Event {
		public var modelId: int;

		public static const SELECTED: String = "modelSelected";

		public function ModelSelectEvent(type:String, modelId: int, bubbles:Boolean=true, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.modelId = modelId;
		}

		override public function clone(): Event {
			return new ModelSelectEvent(type, modelId, bubbles, cancelable);
		}

	}
}