package collab.fm.client.util {
	import collab.fm.client.data.Model;

	import flash.utils.Dictionary;

	public class ModelUtil {

		/* Hierarchy:
		   topNodes: top level Nodes
		   Node: <id, children of type Node>
		 */
		public static function getHierarchy(model: Model): Object {
			var nodes: Dictionary = new Dictionary();
			for each (var feature: Object in model.features) {
				nodes[feature.id] = {parent: null, children: null};
			}
			for each (var rel: Object in model.binaries) {
				if (rel.type == Cst.BIN_REL_REFINES) {
					if (nodes[rel.left].children == null) {
						nodes[rel.left].children = new Array();
					}
					(nodes[rel.left].children as Array).push(rel.right);
					if (nodes[rel.right].parent == null) {
						nodes[rel.right].parent = 1;
					}
				}
			}

			function addChild(n1: Object, n2: Object): void {
				(n1.children as Array).push(n2);
			}

			function newNode(id: Object): Object {
				var n: Object = new Object();
				n.id = id as Number;
				if (nodes[id].children == null) {
					n.children = null;
				} else {
					n.children = new Array();
					for each (var child: Object in nodes[id].children) {
						(n.children as Array).push(newNode(child));
					}
				}
				return n;
			}

			var result: Object = new Object();
			result.topNodes = new Array();

			for (var nodeId: Object in nodes) {
				if (nodes[nodeId].parent == null) {
					(result.topNodes as Array).push(newNode(nodeId));
				}
			}

			return result;
		}

		/**
		 * Sort by approval rating (yesNumber / totalNumer)
		 */
		public static function sortOnRating(
			target: Array,
			uYesPropName: String,  // property name of 'user-yes'
			uNoPropName: String,   // property name of 'user-no'
			myId: int): void {

			function compareRatedItems(a: Object, b: Object): Number {
				var meInA: Boolean = (a[uYesPropName] as Array).indexOf(myId) >= 0;
				var meNotA: Boolean = (a[uNoPropName] as Array).indexOf(myId) >= 0;
				var meInB: Boolean = (b[uYesPropName] as Array).indexOf(myId) >= 0;
				var meNotB: Boolean = (b[uNoPropName] as Array).indexOf(myId) >= 0;

				var isA: Boolean = meInA || meNotB;
				var isB: Boolean = meInB || meNotA;

				if (isA && !isB) {
					// a goes before b
					return -1;
				}
				if (isB && !isA) {
					// a goes behind b
					return 1;
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