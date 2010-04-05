package collab.fm.client.event {
	import flash.events.Event;

	public class OperationCommitEvent extends Event {
		public static const COMMIT_SUCCESS: String = "OperationCommitSuccess";
		public static const FORWARDED: String = "OperationForwarded";

		public var operations: Array;

		public function OperationCommitEvent(type:String, operations: Array, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.operations = operations;
		}

		override public function clone(): Event {
			return new OperationCommitEvent(type, operations, bubbles, cancelable);
		}

	}
}