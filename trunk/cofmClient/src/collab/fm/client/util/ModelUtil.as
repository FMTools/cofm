package collab.fm.client.util {
	import collab.fm.client.data.*;
	
	import mx.collections.IViewCursor;
	import mx.collections.XMLListCollection;

	public class ModelUtil {

		public static function isTrue(b: String): Boolean {
			return b.toLowerCase() == (new Boolean(true).toString().toLowerCase());	
		}
		
		public static function getFeatureNameById(fId: String): String {
			// First, try to get name from working tree
			var n: String = WorkingTreeData.instance.getFeatureNameById(fId);
			if (n != null) {
				return n;
			}
			// Then try to get name from global tree
			n = GlobalTreeData.instance.getFeatureNameById(fId);
			if (n != null) {
				return n;
			}
			return "#" + fId;
		}
		
		public static function clearXMLList(list: XMLList): void {
			for (var i: int = list.length() - 1; i >= 0; i--) {
				delete list[i];
			}
		}

		// TODO: move this method into FeaureModel
		// return false if there are no "YES" voters.
		public static function updateVoters(vote: String, userId: String, root: XML): Boolean {
			var user: XML = <user>{userId}</user>;
			var userInYes: Boolean = XMLList(root.yes.user).contains(user);
			var userInNo: Boolean = XMLList(root.no.user).contains(user);
			if (isTrue(vote)) {
				if (!userInYes) {
					XML(root.yes[0]).appendChild(user);
					if (userInNo) {
						delete root.no.user.(text().toString() == userId)[0];
					}
				}
			} else {
				if (!userInNo) {
					XML(root.no[0]).appendChild(user);
					if (userInYes) {
						delete root.yes.user.(text().toString() == userId)[0];
					}
				}
			}
			return XMLList(root.yes.user).length() > 0;
		}

		/**
		 * Sort by approval rating (yesNumber / totalNumer)
		 */
		public static function sortOnRating(
			target: Array,
			uYesPropName: String,  // property name of 'user-yes'
			uNoPropName: String,   // property name of 'user-no'
			myId: int=-1,        // my user ID
			uValPropName: String=null, // property name of item value
			matchVal: Object=null  // do matching on item value 
			): void {

			function compareRatedItems(a: Object, b: Object): Number {
				/**  The algorithm:
				 *   if (matchVal != null) {
				 *      if (match val in A && not in B) return A before B (-1)
				 *      if (match val in B && not in A) return A after B (1)
				 *   }
				 *   if (myId != -1) {  // >= 0 actually
				 *      if (I'm in A.yes && not in B.yes) return -1
				 *      if (I'm in B.yes && not in A.yes) return 1
				 *      if (I'm in A.no && not in B.no) return 1
				 *      if (I'm in B.no && not in A.no) return -1
				 *   }
				 *   Now decide by the overall rating (yes/total)
				 *   if (A.yes / A.total > B.yes / B.total) return -1
				 *   if (... < ...) return 1
				 *   return 0  // cannot decide which one is more important
				 */
				if (matchVal != null) {
					var valInA: Boolean = (matchVal == a[uValPropName]);
					var valInB: Boolean = (matchVal == b[uValPropName]);
					if (valInA && !valInB) {
						return -1;
					}
					if (valInB && !valInA) {
						return 1;
					}
				}
				if (myId >= 0) {
					var meInA: Boolean = (a[uYesPropName] as Array).indexOf(myId) >= 0;
					var meNotA: Boolean = (a[uNoPropName] as Array).indexOf(myId) >= 0;
					var meInB: Boolean = (b[uYesPropName] as Array).indexOf(myId) >= 0;
					var meNotB: Boolean = (b[uNoPropName] as Array).indexOf(myId) >= 0;

					var isA: Boolean = meInA || meNotB;
					var isB: Boolean = meInB || meNotA;

					if (isA && !isB) {
						return -1;
					}
					if (isB && !isA) {
						return 1;
					}
				}
				// compare by the approval ratings
				// ( a.YES / a.Total ) compares with ( b.YES / b.Total)
				var aYes: int = (a[uYesPropName] as Array).length;
				var aTotal: int = (a[uNoPropName] as Array).length + aYes;
				var bYes: int = (b[uYesPropName] as Array).length;
				var bTotal: int = (b[uNoPropName] as Array).length + bYes;

				var aybt: int = aYes * bTotal;
				var byat: int = bYes * aTotal;
				if (aybt > byat) return -1; // a first
				if (aybt < byat) return 1; // b first
				return 0;
			}
			target.sort(compareRatedItems);
		}
	}
}