package collab.fm.client.event {
	import flash.events.Event;

	public class ModelSelectEvent extends Event {
		public var modelId: int;
		public var modelName: String;

		public static const SELECTED: String = "modelSelected";

		public function ModelSelectEvent(type:String, modelId: int, modelName: String, bubbles:Boolean=true, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.modelId = modelId;
			this.modelName = modelName;
		}

		override public function clone(): Event {
			return new ModelSelectEvent(type, modelId, modelName, bubbles, cancelable);
		}

	}
}