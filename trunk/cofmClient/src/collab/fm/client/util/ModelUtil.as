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
				if (rel.type == Resources.BIN_REL_REFINES) {
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

	}
}