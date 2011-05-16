package cofm.event 
{
	import flash.events.Event;

	public class PageSwitchEvent extends Event
	{
		
		public static const RETURN_TO_HOME_PAGE: String = "returnToHomePage";
		public static const OTHERS_EXIT_WORK_PAGE: String = "othersExitWorkPage";
		public static const ENTER_WORK_PAGE: String = "enterWorkPage";
		public static const ENTER_HOME_PAGE: String = "enterHomePage";
		
		public var user: int;
		public var model: int;
		
		public function PageSwitchEvent(type:String, user: int=-1, model: int=-1, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.user = user;
			this.model = model;
		}
		
		override public function clone():Event {
			return new PageSwitchEvent(type, user, model, bubbles, cancelable);
		}
		
	}
}