package collab.fm.client.data {
	import flash.utils.Dictionary;

	// The Feature Model
	public class Model {
		/* Definition of the Model: (See also clientServer.protocol.updateResponse)
		   features: array of feature
		   binaries: array of binary relationships

		   feature:
		   - id: Long
		   - name: SORTED array of name2
		   - des: SORTED array of des2
		   - uYes: sorted array of supporter ID
		   - uNo: sorted array of opponent ID
		   - uOptYes: sorted array of optionality supporter ID
		   - uOptNo: sorted array of optionality opponent ID
		   - rels: sorted array of involved relationship ID
		   name2/des2:
		   - val: String, the value
		   - uYes: sorted array of supporter ID
		   - uNo: sorted array of opponent ID
		   (Sort: see ModelUtil.sortByRating)
		   binary relationship:
		   - id: Long
		   - type: String
		   - uYes: sorted array of supporter ID
		   - uNo: sorted array of opponent ID
		   - left: Long, left feature ID
		   - right: Long, right feature ID
		 */
		// Features, key = id
		public var features: Dictionary;
		// Binary Relationships, key = id
		public var binaries: Dictionary;
		// My user ID
		public var myId: int;

		// Data Views
		private var views: Array;

		public function Model() {
		}

		public function updateViews(minorChange: Object): void {
			for each (var view: Object in views) {
				(view as DataView).refresh(minorChange);
			}
		}
	}
}