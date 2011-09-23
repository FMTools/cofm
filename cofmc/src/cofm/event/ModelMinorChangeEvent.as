package cofm.event 
{
	import flash.events.Event;

	public class ModelMinorChangeEvent extends Event {
		public static const FEATURE_CREATED_LOCALLY: String = "FeatureCreatedLocally";

		public var data: Object;
		public var sourceCommand: int;

		public function ModelMinorChangeEvent(type:String, data: Object, sourceCmd: int = -1, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.data = data;
			this.sourceCommand = sourceCmd;
		}

		override public function clone(): Event {
			return new ModelMinorChangeEvent(type, data, sourceCommand, bubbles, cancelable);
		}

	}
}