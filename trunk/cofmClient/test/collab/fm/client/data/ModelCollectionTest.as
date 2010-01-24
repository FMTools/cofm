package collab.fm.client.data {
	import collab.fm.client.util.Cst;

	import flexunit.framework.TestCase;

	public class ModelCollectionTest extends TestCase {

		public function testModelCollectionUpdateView(): void {
			var input: Array = [
				{ id: 1, users: [1, 2, 3, 4, 5], 
					names: [
					{val: "Avatar", v1: [1, 4], v0: [2, 3, 5] },
						{val: "Avanti", v1: [1, 2, 3, 4], v0: [5] }
					],
					dscs: [
					{val: "gogogogo", v1: [3, 5], v0: [1, 2, 4] }
					]
				},
				{ id: 2, users: [14, 2, 3, 4, 5, 9, 10], 
					names: [
					{val: "Avatar123", v1: [1, 9, 10, 11, 4], v0: [2, 3, 5] },
						{val: "Avanti4321", v1: [1, 2, 3, 4], v0: [5] }
					],
					dscs: [
					{val: "gogogogo", v1: [3, 5], v0: [1, 2, 4] }
					]
				}
				];
			ModelCollection.instance.refresh(input);
			trace("------------ ModelCollection.updateEntireView ------------");
			trace("my = " + ModelCollection.instance.my);
			trace("others = " + ModelCollection.instance.others);
			trace("-----------------------------------------------------------");
			ModelCollection.instance.refresh({
					"event": Cst.DATA_MY_INFO,
					"myId": 1,
					"myName": "haha"
				}, true);
			trace("------------ ModelCollection.updateMinorChange ------------");
			trace("my = " + ModelCollection.instance.my);
			trace("others = " + ModelCollection.instance.others);
			trace("-----------------------------------------------------------");
			ModelCollection.instance.refresh({
					"event": Cst.DATA_USER_NAMES,
					"list": { "1": "hendry", "2": "ronnie" }
				}, true);
			trace("------------ ModelCollection.updateMinorChange ------------");
			trace("my = " + ModelCollection.instance.my);
			trace("others = " + ModelCollection.instance.others);
			trace("-----------------------------------------------------------");
		}
	}
}