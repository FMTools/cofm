package collab.fm.client.data {
	import collab.fm.client.util.Cst;

	import flexunit.framework.TestCase;

	public class ModelCollectionTest extends TestCase {

		public function testModelCollectionUpdateView(): void {
			var input: Array = [
				{ id: 1, user: [1, 2, 3, 4, 5], 
					name: [
					{val: "Avatar", uYes: [1, 4], uNo: [2, 3, 5] },
						{val: "Avanti", uYes: [1, 2, 3, 4], uNo: [5] }
					],
					des: [
					{val: "gogogogo", uYes: [3, 5], uNo: [1, 2, 4] }
					]
				},
				{ id: 2, user: [14, 2, 3, 4, 5, 9, 10], 
					name: [
					{val: "Avatar123", uYes: [1, 9, 10, 11, 4], uNo: [2, 3, 5] },
						{val: "Avanti4321", uYes: [1, 2, 3, 4], uNo: [5] }
					],
					des: [
					{val: "gogogogo", uYes: [3, 5], uNo: [1, 2, 4] }
					]
				}
				];
			ModelCollection.instance.refresh(input);
			var info: ModelInfo = new ModelInfo(1);
			trace("------------ ModelCollection.updateEntireView ------------");
			trace("my = " + ModelCollection.instance.my);
			trace("others = " + ModelCollection.instance.others);
			trace("No.1 = " + info.users.toString());
			trace("-----------------------------------------------------------");
			ModelCollection.instance.refresh({
					"event": Cst.DATA_MY_INFO,
					"myId": 1,
					"myName": "haha"
				}, true);
			trace("------------ ModelCollection.updateMinorChange ------------");
			trace("my = " + ModelCollection.instance.my);
			trace("others = " + ModelCollection.instance.others);
			trace("No.1 = " + info.users.toString());
			trace("-----------------------------------------------------------");
			ModelCollection.instance.refresh({
					"event": Cst.DATA_USER_NAMES,
					"list": { "1": "hendry", "2": "ronnie" }
				}, true);
			trace("------------ ModelCollection.updateMinorChange ------------");
			trace("my = " + ModelCollection.instance.my);
			trace("others = " + ModelCollection.instance.others);
			trace("No.1 = " + info.users.toString());
			trace("-----------------------------------------------------------");
		}
	}
}