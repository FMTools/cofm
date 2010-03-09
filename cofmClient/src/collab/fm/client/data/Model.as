package collab.fm.client.data {
	import flash.utils.Dictionary;

	// The Feature Model
	public class Model {
		/*
		 */
		// Features, key = id
		public var features: Dictionary;
		// Binary Relationships, key = id
		public var binaries: Dictionary;

		private static var _model: Model = new Model();

		public static function get instance(): Model {
			return _model;
		}

		public function Model() {
			super();
		}

	}
}