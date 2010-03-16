package collab.fm.client.event {
	import flash.events.Event;

	public class ModelSearchEvent extends Event {
		public static const SUCCESS: String = "modelSearchSuccess";

		public var searchWord: String;
		public var result: Array;
		public var exactlyMatches: Boolean;

		public function ModelSearchEvent(type:String, searchWord: String, result: Array, exactlyMatches: Boolean, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.searchWord = searchWord;
			this.result = result;
			this.exactlyMatches = exactlyMatches;
		}

		override public function clone(): Event {
			return new ModelSearchEvent(type, searchWord, result, exactlyMatches, bubbles, cancelable);
		}

	}
}