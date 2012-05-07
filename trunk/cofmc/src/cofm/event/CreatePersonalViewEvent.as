package cofm.event
{
	import flash.events.Event;
	
	public class CreatePersonalViewEvent extends Event
	{
		public static const SUCCESS: String = "CreatePersonalViewSuccess";
		
		public var data: Object;
		public function CreatePersonalViewEvent(type:String, data: Object, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.data = data;
		}
		
		override public function clone():Event {
			return new CreatePersonalViewEvent(type, data, bubbles, cancelable);
		}
	}
}