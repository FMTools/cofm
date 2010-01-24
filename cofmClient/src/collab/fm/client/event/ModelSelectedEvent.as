package collab.fm.client.event {
	import flash.events.Event;

	public class ModelSelectedEvent extends Event {
		public var modelId: int;

		public static const SELECTED: String = "modelSelected";

		public function ModelSelectedEvent(type:String, modelId: int, bubbles:Boolean=true, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.modelId = modelId;
		}

		override public function clone(): Event {
			return new ModelSelectedEvent(type, modelId, bubbles, cancelable);
		}

	}
}