package cofm.event 
{
	import flash.events.Event;
	
	public class PersonalViewUpdateEvent extends Event {
		public static const SUCCESS: String = "PersonalViewUpdateSuccess";
		public var pv: Object;
		
		public function PersonalViewUpdateEvent(type:String, pv: Object, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.pv = pv;
		}
		
		override public function clone(): Event {
			return new PersonalViewUpdateEvent(type, pv, bubbles, cancelable);
		}
	}
}