package collab.fm.client.event
{
	import flash.events.Event;

	public class ConsoleEvent extends Event
	{
		public static const APPEND: String = "appendToConsole";
		public var msg: String;
		public function ConsoleEvent(type:String, msg: String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.msg = msg;
		}
		
		override public function clone():Event {
			return new ConsoleEvent(type, msg, bubbles, cancelable);
		}
		
	}
}