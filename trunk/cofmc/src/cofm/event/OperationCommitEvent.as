package cofm.event 
{
	import flash.events.Event;

	public class OperationCommitEvent extends Event {
		public static const COMMIT_SUCCESS: String = "OperationCommitSuccess";
		public static const FORWARDED: String = "OperationForwarded";
		public static const EXECUTED_ON_LOCAL: String = "OperationExecutedLocally";

		public var response: Object;

		public function OperationCommitEvent(type:String, response: Object, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.response = response;
		}

		override public function clone(): Event {
			return new OperationCommitEvent(type, response, bubbles, cancelable);
		}

	}
}