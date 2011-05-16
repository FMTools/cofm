package cofm.event
{
	import flash.events.Event;
	
	public class AddCommentEvent extends Event
	{
		public static const SUCCESS: String = "AddCommentSuccess";
		
		public var user: int;
		public var feature: int;
		public var content: String;
		public var time: String;
		
		public function AddCommentEvent(type:String, user: int, feature: int, content: String, time: String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.user = user;
			this.feature = feature;
			this.content = content;
			this.time = time;
		}
		
		override public function clone():Event {
			return new AddCommentEvent(type, user, feature, content, time, bubbles, cancelable);
		}
	}
}