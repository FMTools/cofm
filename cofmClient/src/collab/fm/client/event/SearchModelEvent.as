package collab.fm.client.event {
	import flash.events.Event;

	public class SearchModelEvent extends Event {
		public var searchWord: String;
		public var exactlyMatches: Boolean;

		public static const SUCCESS: String = "searchModelSuccess";

		public function SearchModelEvent(type:String, searchWord: String, exactlyMatches: Boolean, bubbles:Boolean=true, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this.searchWord = searchWord;
			this.exactlyMatches = exactlyMatches;
		}

		override public function clone(): Event {
			return new SearchModelEvent(type, searchWord, exactlyMatches, bubbles, cancelable);
		}

	}
}