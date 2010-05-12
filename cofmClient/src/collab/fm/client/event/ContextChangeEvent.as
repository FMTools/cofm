package collab.fm.client.event
{
	import flash.events.Event;

	public class ContextChangeEvent extends Event
	{
		
		public static const RETURN_TO_HOME_PAGE: String = "returnToHomePage";
		public static const ENTER_WORK_PAGE: String = "enterWorkPage";
		
		public function ContextChangeEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		
		override public function clone():Event {
			return new ContextChangeEvent(type, bubbles, cancelable);
		}
		
	}
}