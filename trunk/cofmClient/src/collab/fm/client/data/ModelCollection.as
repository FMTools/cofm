package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import flash.utils.Dictionary;

	import mx.collections.IViewCursor;
	import mx.collections.XMLListCollection;
	import mx.utils.StringUtil;

	public class ModelCollection {
		public var currentModelId: int = -1;

		// TODO: 1. calculate the primary name and description
		// 2. When the data was first loaded, separate it into "my" and "others"
		// 3. calculate the number of contributers
		private static const _defaultBinding: XML = <model id="0" name={RS.M_LIST_EMPTY_LIST_INFO} userNum="0">
				<des></des>
				<users>
					<user></user>
				</users>
			</model>;

		private var _my: XMLListCollection;
		private var _others: XMLListCollection;
		private var _all: XML = <all/>;

		public var lastSearchHits: Boolean;

		private static var _instance: ModelCollection = new ModelCollection();

		public static function get instance(): ModelCollection {
			return _instance;
		}

		public function ModelCollection() {
			super();
			_my = new XMLListCollection(new XMLList(_defaultBinding));
			_others = new XMLListCollection(new XMLList(_defaultBinding));

			ClientEvtDispatcher.instance().addEventListener(LoginEvent.SUCCESS, onLogin);
			ClientEvtDispatcher.instance().addEventListener(ListUserEvent.SUCCESS, onListUser);
			ClientEvtDispatcher.instance().addEventListener(ModelSearchEvent.SUCCESS, onSearch);
			ClientEvtDispatcher.instance().addEventListener(ModelCreateEvent.SUCCESS, onCreate);
			ClientEvtDispatcher.instance().addEventListener(ModelSelectEvent.SELECTED, onSelect);
		}

		private function resetSource(dst: XMLListCollection, src: XML): void {
			dst.removeAll();
			var list: XMLList = new XMLList(src.model);
			if (list.length() <= 0) {
				dst.source = new XMLList(_defaultBinding);
			} else {
				dst.source = list;
			}
		}

		private function onLogin(evt: LoginEvent): void {
			// Find all models that contain me as a user, add to "my" and remove from "others"
			var me: XML = <user>{evt.myId}</user>;
			var me2: XML = <user>{evt.myName}</user>;
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

		private function onListUser(evt: ListUserEvent): void {
			doUserIdToName(my.source..user, evt.users);
			doUserIdToName(others.source..user, evt.users);
		}

		private function onSearch(evt: ModelSearchEvent): void {
			var updateMyList: Boolean = my.contains(<user></user>); // My list hasn't created yet.
			var myXml: XML = <my/>;
			var othersXml: XML = <others/>;
			if (evt.searchWord == null || StringUtil.trim(evt.searchWord) == "") {
				// Always hits on empty keyword
				lastSearchHits = true;
			} else {
				lastSearchHits = false;
			}
			for each (var _model: Object in evt.result) {
				var rslt: Object = createXmlFromModel(_model, evt.searchWord);
				if (updateMyList && rslt.isMine == true) {
					myXml.appendChild(rslt.xml);
				} else {
					if (rslt.exactlyMatch) {
						othersXml.insertChildAfter(null, rslt.xml);
						lastSearchHits = true;
					} else {
						othersXml.appendChild(rslt.xml);
					}
				}
				_all.appendChild(XML(rslt.xml).copy());
			}
			if (updateMyList) {
				resetSource(my, myXml);
			} else {
				doUserIdToName(othersXml..user);
			}
			resetSource(others, othersXml);

			if (lastSearchHits) {
				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelSearchEvent(ModelSearchEvent.EXACTLY_MATCHES, evt.searchWord, evt.result));
			}
		}

		private function onCreate(evt: ModelCreateEvent): void {
			my.addItem(evt.model);
		}

		private function onSelect(evt: ModelSelectEvent): void {
			this.currentModelId = evt.modelId;
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
		private function createXmlFromModel(input: Object, search: String = null): Object {
			var _isMine: Boolean = false;
			var _id: int = int(input.id);
			var _totalUserNum: int = 0;
			var _users: XML = <users></users>;
			for each (var u: Object in(input.users as Array)) {
				++_totalUserNum;
				_users.appendChild(<user>{int(u)}</user>);
				if (int(u) == UserList.instance.myId) {
					_isMine = true;
				}
			}

			var _exactlyMatch: Boolean = false;
			// Sort the name, if we are searching name, then the searched name should become first
			_exactlyMatch = ModelUtil.sortOnRating(
				input.names as Array, "v1", "v0", UserList.instance.myId,
				"val", search);
			var _primary_name: String = (input.names as Array)[0].val; // primary == first

			ModelUtil.sortOnRating(input.dscs as Array, "v1", "v0", UserList.instance.myId);
			var _primary_des: String = (input.dscs as Array)[0].val;

			var result: XML = 
				<model isMine={_isMine} id={_id} name={_primary_name} userNum={_totalUserNum}>
					<des>{_primary_des}</des>
				</model>;
			result.appendChild(_users);

			return {"xml": result, "isMine": _isMine, "exactlyMatch": _exactlyMatch};
		}

		private function doUserIdToName(list: XMLList, input: Dictionary = null): void {
			for each (var u: Object in list) {
				var theId: String = XML(u).text().toString();
				if (input != null) {
					var name: String = input[theId];
					if (name) {
						XML(u).setChildren(name);
					}
				} else {
					var userWithTheId: XMLList = UserList.instance.users.source.(@id==theId);
					if (userWithTheId.length() > 0) {
						XML(u).setChildren(userWithTheId[0].@name);
					}
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