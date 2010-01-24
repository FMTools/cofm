package collab.fm.client.data {
	import collab.fm.client.util.*;

	import mx.collections.IViewCursor;
	import mx.collections.XMLListCollection;

	public class ModelCollection extends AbstractDataView {

		// TODO: 1. calculate the primary name and description
		// 2. When the data was first loaded, separate it into "my" and "others"
		// 3. calculate the number of contributers
		private static const _defaultEmptyModel: XML = <model id="0" name={RS.M_LIST_EMPTY_LIST_INFO} userNum="0">
				<des></des>
				<users>
					<user></user>
				</users>
			</model>;

		private var _my: XMLListCollection;
		private var _others: XMLListCollection;
		private var _all: XML = <all/>;

		public var lastSearchHits: Boolean;

		private static var _mcol: ModelCollection = new ModelCollection();

		public static function get instance(): ModelCollection {
			return _mcol;
		}

		public function ModelCollection() {
			super();
			_my = new XMLListCollection(new XMLList(_defaultEmptyModel));
			_others = new XMLListCollection(new XMLList(_defaultEmptyModel));
		}

		private function resetSource(dst: XMLListCollection, src: XML): void {
			dst.removeAll();
			var list: XMLList = new XMLList(src.model);
			if (list.length() <= 0) {
				dst.source = new XMLList(_defaultEmptyModel);
			} else {
				dst.source = list;
			}
		}

		/**
		 * Input format: (see server.ListModelResponse for details)
		 *    Array of {
		 *       id: Long, users: [Long],
		 *       names: [
		 *          {val: String, v1: [Long], v0: [Long]}
		 *       ],
		 *       dscs: [ (same as names) ]
		 *    }
		 */
		private function createModelAsXml(input: Object, search: String = null): Object {
			var _isMine: Boolean = false;
			var _id: int = int(input.id);
			var _totalUserNum: int = 0;
			var _users: XML = <users></users>;
			for each (var u: Object in(input.users as Array)) {
				++_totalUserNum;
				_users.appendChild(<user>{int(u)}</user>);
				if (int(u) == User.instance.myId) {
					_isMine = true;
				}
			}

			var _exactlyMatch: Boolean = false;
			// Sort the name, if we are searching name, then the searched name should become first
			_exactlyMatch = ModelUtil.sortOnRating(
				input.names as Array, "v1", "v0", User.instance.myId,
				"val", search);
			var _primary_name: String = (input.names as Array)[0].val; // primary == first

			ModelUtil.sortOnRating(input.dscs as Array, "v1", "v0", User.instance.myId);
			var _primary_des: String = (input.dscs as Array)[0].val;

			var result: XML = 
				<model isMine={_isMine} id={_id} name={_primary_name} userNum={_totalUserNum}>
					<des>{_primary_des}</des>
				</model>;
			result.appendChild(_users);

			return {"xml": result, "isMine": _isMine, "exactlyMatch": _exactlyMatch};
		}


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
				_all.appendChild(XML(rslt.xml).copy());
			}
			resetSource(my, myXml);
			resetSource(others, othersXml);
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
				case Cst.DATA_CREATE_MODEL:
					my.addItem(input["model"]);
					break;
			}
		}

		private function updateOthersModel(input: Object): void {
			if (input.searchWord == "") {
				// Always hits on empty keyword
				lastSearchHits = true;
			} else {
				lastSearchHits = false;
			}
			var othersXml: XML = <others></others>;
			for each (var _model: Object in(input.models as Array)) {
				var rslt: Object = createModelAsXml(_model, input.searchWord);
				if (rslt.exactlyMatch) {
					othersXml.insertChildAfter(null, rslt.xml);
					lastSearchHits = true;
				} else {
					othersXml.appendChild(rslt.xml);
				}
			}
			changeUserIdToName(othersXml..user);
			resetSource(others, othersXml);
		}

		private function changeUserIdToName(list: XMLList): void {
			for each (var u: Object in list) {
				var theId: String = XML(u).text().toString();
				var userWithTheId: XMLList = User.instance.users.source.(@id==theId);
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
			var list: XMLListCollection = new XMLListCollection(new XMLList(_all.model));
			var myModels: XML = <my/>;
			var cursor: IViewCursor = list.createCursor();
			while (!cursor.afterLast) {
				if (XMLList(cursor.current.users.user).contains(me)
					|| XMLList(cursor.current.users.user).contains(me2)) {
					myModels.appendChild(XML(cursor.current).copy());
				}
				cursor.moveNext();
			}
			resetSource(my, myModels);
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