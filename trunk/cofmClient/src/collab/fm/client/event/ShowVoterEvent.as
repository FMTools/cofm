package collab.fm.client.event
{
	import flash.events.Event;

	public class ShowVoterEvent extends Event
	{
		public static const SHOW_VOTER_NAME: String = "showVoterName";
		
		public var y: Array;
		public var n: Array;
		public function ShowVoterEvent(type:String, y: Array, n: Array, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.y = y;
			this.n = n;
		}
		
		override public function clone():Event {
			return new ShowVoterEvent(type, y, n, bubbles, cancelable);
		}
		
	}
}