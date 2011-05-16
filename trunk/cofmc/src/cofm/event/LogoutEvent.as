package cofm.event 
{
	import flash.events.Event;

	public class LogoutEvent extends Event
	{
		public static const LOGGED_OUT: String = "loggedOut";
		
		public var user: int;
		public function LogoutEvent(type:String, user: int, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.user = user;		
		}
		
		override public function clone():Event {
			return new LogoutEvent(type, user, bubbles, cancelable);
		}
	}
}