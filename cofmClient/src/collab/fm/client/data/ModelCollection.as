package collab.fm.client.data {
	import collab.fm.client.util.*;

	import mx.collections.IViewCursor;
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
			switch (input.event) {
				case Cst.DATA_MY_INFO:
					updateMyList(input);
					break;
				case Cst.DATA_USER_NAMES:
					updateUserIdToName(input);
					break;
				case Cst.DATA_OTHERS_MODEL:
					updateOthersModel(input);
					break;
			}
		}

		private function updateOthersModel(input: Object): void {
			var othersXml: XML = <others></others>;
			for each (var _model: Object in(input.models as Array)) {
				var rslt: Object = createModelAsXml(_model);
				if (rslt.isMine == false) {
					othersXml.appendChild(rslt.xml);
				}
			}
			others.removeAll();
			others = new XMLListCollection(new XMLList(othersXml.model));
			changeUserIdToName(others.source..user);
		}

		private function changeUserIdToName(list: XMLList): void {
			for each (var u: Object in list) {
				var theId: String = XML(u).text().toString();
				var userWithTheId: XMLList = User.instance.users.source.user.(@id==theId);
				if (userWithTheId.length() > 0) {
					XML(u).setChildren(userWithTheId[0].@name);
				}
			}
		}

		private function updateUserIdToName(input: Object): void {
			doUserIdToName(my.source..user, input);
			doUserIdToName(others.source..user, input);
		}

		private function doUserIdToName(list: XMLList, input: Object): void {
			for each (var u: Object in list) {
				var name: String = input.list[XML(u).text().toString()];
				if (name) {
					XML(u).setChildren(name);
				}
			}
		}

		private function updateMyList(input: Object): void {
			// Find all models that contain me as a user, add to "my" and remove from "others"
			var me: XML = <user>{input.myId}</user>;
			var me2: XML = <user>{input.myName}</user>;
			var cursor: IViewCursor = others.createCursor();
			while (!cursor.afterLast) {
				if (XMLList(cursor.current.users.user).contains(me)
					|| XMLList(cursor.current.users.user).contains(me2)) {
					my.addItem(XML(cursor.current).copy());
					cursor.remove();
				} else {
					cursor.moveNext();
				}
			}
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