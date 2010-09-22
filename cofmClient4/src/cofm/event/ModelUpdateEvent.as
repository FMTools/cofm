package cofm.event 
{
	import flash.events.Event;

	public class ModelUpdateEvent extends Event {
		public static const SUCCESS: String = "ModelUpdateSuccess";
		public static const LOCAL_MODEL_COMPLETE: String = "LocalModelComplete";
		public static const WORKING_VIEW_COMPLETE: String = "WorkingViewComplete";
		public var model: Object;

		public function ModelUpdateEvent(type:String, model: Object, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.model = model;
		}

		override public function clone(): Event {
			return new ModelUpdateEvent(type, model, bubbles, cancelable);
		}
	}
}