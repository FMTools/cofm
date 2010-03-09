package collab.fm.client.event {
	import flash.events.Event;

	public class ModelSearchEvent extends Event {
		public static const SUCCESS: String = "modelSearchSuccess";
		public static const EXACTLY_MATCHES: String = "modelExactlyMatches";

		public var searchWord: String;
		public var result: Array;

		public function ModelSearchEvent(type:String, searchWord: String, result: Array, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.searchWord = searchWord;
			this.result = result;
		}

		override public function clone(): Event {
			return ModelSearchEvent(type, searchWord, result, bubbles, cancelable);
		}

	}
}