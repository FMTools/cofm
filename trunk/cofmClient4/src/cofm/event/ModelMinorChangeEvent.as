package cofm.event 
{
	import flash.events.Event;

	public class ModelMinorChangeEvent extends Event {
		public static const FEATURE_CREATED_LOCALLY: String = "FeatureCreatedLocally";

		public var data: Object;

		public function ModelMinorChangeEvent(type:String, data: Object, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.data = data;
		}

		override public function clone(): Event {
			return new ModelMinorChangeEvent(type, data, bubbles, cancelable);
		}

	}
}