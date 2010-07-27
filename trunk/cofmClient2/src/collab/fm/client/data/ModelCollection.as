package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	import mx.collections.XMLListCollection;

	/**
	 * 2010.05.06: Removed the "my" (model list) data in this version.
	 */
	public class ModelCollection {
		public var currentModelId: int = -1;

		//private var _my: XMLListCollection;
		private var _others: XMLListCollection;
		private var _all: XML = <all/>;

		public var lastSearchHits: Boolean;

		private static var _instance: ModelCollection = new ModelCollection();

		public static function get instance(): ModelCollection {
			return _instance;
		}

		public function ModelCollection() {
			//_my = new XMLListCollection(new XMLList(_defaultBinding));
			_others = new XMLListCollection(new XMLList());

			ClientEvtDispatcher.instance().addEventListener(LoginEvent.SUCCESS, onLogin);
			ClientEvtDispatcher.instance().addEventListener(ModelSearchEvent.SUCCESS, onSearch);
			ClientEvtDispatcher.instance().addEventListener(ModelCreateEvent.SUCCESS, onCreate);
			ClientEvtDispatcher.instance().addEventListener(ModelSelectEvent.SELECTED, onSelect);

		}

		public function getModelNameById(id: int): String {
			//var m: XMLList = this.my.source.(@id==String(id));
			//if (m.length() > 0) {
			//	return m[0].@name;
			//}
			var m2: XMLList = this.others.source.(@id==String(id));
			if (m2.length() > 0) {
				return m2[0].@name;
			}
			return "null";
		}

		private function resetSource(dst: XMLListCollection, src: XML): void {
			dst.removeAll();
			var list: XMLList = new XMLList(src.model);
			if (list.length() <= 0) {
				dst.source = new XMLList();
			} else {
				dst.source = list;
			}
		}

		private function onLogin(evt: LoginEvent): void {
		/*
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
		 */
		}

		private function onSearch(evt: ModelSearchEvent): void {
			//var updateMyList: Boolean = my.contains(<user></user>); // My list hasn't created yet.
			//var myXml: XML = <my/>;
			var othersXml: XML = <others/>;
			for each (var _model: Object in evt.result) {
				var rslt: Object = createXmlFromModel(_model, evt.searchWord);
				//if (updateMyList && rslt.isMine == true) {
				//	myXml.appendChild(rslt.xml);
				//} else {
				if (evt.exactlyMatches) {
					// Put in the front
					othersXml.insertChildAfter(null, rslt.xml);
				} else {
					// Append at the end
					othersXml.appendChild(rslt.xml);
				}
				//}
				_all.appendChild(XML(rslt.xml).copy());
			}
			//if (updateMyList) {
			//	resetSource(my, myXml);
			//} else {
			doUserIdToName(othersXml..user);
			//}
			resetSource(others, othersXml);
		}

		private function onCreate(evt: ModelCreateEvent): void {
			//my.addItem(evt.model);
			others.addItem(evt.model);
		}

		private function onSelect(evt: ModelSelectEvent): void {
			this.currentModelId = evt.modelId;
		}

		/**
		 * Input format: (see server.ListModelResponse for details)
		 *    Array of {
		 *       id, cid, ctime, users, attrs: [
		 *           { name, ..., vals: [ {val, v1, v0} ...] }
		 *       ]
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

			var result: XML = 
				<model isMine={_isMine} id={_id} creator={input.cid} time={input.ctime} 
					name={input.name} userNum={_totalUserNum}>
					<des>{input.des}</des>
				</model>;
			result.appendChild(_users);

			return {"xml": result, "isMine": _isMine};
		}

		private function doUserIdToName(list: XMLList): void {
			for each (var u: Object in list) {
				var theId: String = XML(u).text().toString();
				var userWithTheId: XMLList = UserList.instance.users.source.(@id==theId);
				if (userWithTheId.length() > 0) {
					XML(u).setChildren(userWithTheId[0].@name);
				}
			}
		}

//		[Bindable]
//		public function get my(): XMLListCollection {
//			return _my;
//		}
//
//		public function set my(xml: XMLListCollection): void {
//			_my = xml;
//		}

		[Bindable]
		public function get others(): XMLListCollection {
			return _others;
		}

		public function set others(xml: XMLListCollection): void {
			_others = xml;
		}
	}
}