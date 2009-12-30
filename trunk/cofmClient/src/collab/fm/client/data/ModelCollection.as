package collab.fm.client.data {
	import collab.fm.client.util.*;

	import mx.collections.XMLListCollection;

	public class ModelCollection extends AbstractDataView {

		// TODO: 1. calculate the primary name and description
		// 2. When the data was first loaded, separate it into "my" and "others"
		// 3. calculate the number of contributers
		private static const _defaultMyModel: XML = <model id="0" name={RS.M_LIST_EMPTY_LIST_INFO} userNum="0">
				<des></des>
			</model>;

		private static const _defaultOtherModel: XML = <model id="0" name={RS.M_LIST_EMPTY_LIST_INFO} userNum="0">
				<des></des>
				<users>
					<user></user>
				</users>
			</model>;

		private var _my: XMLListCollection;
		private var _others: XMLListCollection;

		private static var _mcol: ModelCollection = new ModelCollection();

		public static function get instance(): ModelCollection {
			return _mcol;
		}

		public function ModelCollection() {
			super();
			_my = new XMLListCollection(new XMLList(_defaultMyModel));
			_others = new XMLListCollection(new XMLList(_defaultOtherModel));
		}

		private function createModelAsXml(input: Object): Object {
			var isMine: Boolean = false;
			var _id: int = int(input.id);
			var _totalUserNum: int = 0;
			var _users: XML = <users></users>;
			for each (var u: Object in(input.user as Array)) {
				++_totalUserNum;
				_users.appendChild(<user>{int(u)}</user>);
				if (int(u) == User.instance.myId) {
					isMine = true;
				}
			}
			ModelUtil.sortOnRating(input.name as Array, "uYes", "uNo", User.instance.myId);
			var _primary_name: String = (input.name as Array)[0].val;
			ModelUtil.sortOnRating(input.des as Array, "uYes", "uNo", User.instance.myId);
			var _primary_des: String = (input.des as Array)[0].val;
			var result: XML = 
				<model id={_id} name={_primary_name} userNum={_totalUserNum}>
					<des>{_primary_des}</des>
				</model>;
			result.appendChild(_users);

			return {"xml": result, "isMine": isMine};
		}

		/**
		 * Input format: (see server.ListModelResponse for details)
		 *    Array of {
		 *       id: Long, user: [Long],
		 *       name: [
		 *          {val: String, uYes: [Long], uNo: [Long]}
		 *       ],
		 *       des: [ (same as name) ]
		 *    }
		 */
		override protected function updateEntireData(input: Object): void {
			var myXml: XML = <my></my>;
			var othersXml: XML = <others></others>;
			for each (var _model: Object in(input as Array)) {
				var rslt: Object = createModelAsXml(_model);
				if (rslt.isMine == true) {
					myXml.appendChild(rslt.xml);
				} else {
					othersXml.appendChild(rslt.xml);

				}
			}
			my.removeAll();
			others.removeAll();
			my = new XMLListCollection(new XMLList(myXml.model));
			others = new XMLListCollection(new XMLList(othersXml.model));
		}

		override protected function updateMinorChange(input: Object): void {

		}

		[Bindable]
		public function get my(): XMLListCollection {
			return _my;
		}

		public function set my(xml: XMLListCollection): void {
			_my = xml;
		}

		[Bindable]
		public function get others(): XMLListCollection {
			return _others;
		}

		public function set others(xml: XMLListCollection): void {
			_others = xml;
		}
	}
}