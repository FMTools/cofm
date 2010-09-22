package cofm.event 
{
	import flash.events.Event;

	public class ModelCreateEvent extends Event {

		public static const SUCCESS: String = "modelCreateSuccess";

		public var model:XML;

		public function ModelCreateEvent(type:String, model:XML, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.model = model;
		}

		override public function clone(): Event {
			return new ModelCreateEvent(type, model, bubbles, cancelable);
		}

	}
}