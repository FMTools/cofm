package cofm.model {
	import cofm.event.*;
	
	import mx.collections.XMLListCollection;
	
	import cofm.util.*;

	/**
	 * The basic info of existing feature models.
	 */
	public class ModelCollection {
		public var currentModelId: int = -1;
		
		[Bindable]
		public var models: XMLListCollection;
		
		private var _all: XML = <all/>;

		public var lastSearchHits: Boolean;

		private static var _instance: ModelCollection = new ModelCollection();

		public static function instance(): ModelCollection {
			return _instance;
		}

		public function ModelCollection() {
			models = new XMLListCollection(new XMLList());

			ClientEvtDispatcher.instance().addEventListener(ModelSearchEvent.SUCCESS, onSearch);
			ClientEvtDispatcher.instance().addEventListener(ModelCreateEvent.SUCCESS, onCreate);
			ClientEvtDispatcher.instance().addEventListener(ModelSelectEvent.SELECTED, onSelect);
		}

		public function getModelNameById(id: int): String {
			//var m: XMLList = this.my.source.(@id==String(id));
			//if (m.length() > 0) {
			//	return m[0].@name;
			//}
			var m2: XMLList = this.models.source.(@id==String(id));
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

		private function onSearch(evt: ModelSearchEvent): void {
			var othersXml: XML = <others/>;
			for each (var _model: Object in evt.result) {
				var rslt: Object = createXmlFromModel(_model, evt.searchWord);
				if (evt.exactlyMatches) {
					// Put in the front
					othersXml.insertChildAfter(null, rslt.xml);
				} else {
					// Append at the end
					othersXml.appendChild(rslt.xml);
				}
				_all.appendChild(XML(rslt.xml).copy());
			}
			doUserIdToName(othersXml..user);
			resetSource(models, othersXml);
		}

		private function onCreate(evt: ModelCreateEvent): void {
			//my.addItem(evt.model);
			models.addItem(evt.model);
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
				if (int(u) == UserList.instance().myId) {
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
				var userWithTheId: XMLList = UserList.instance().users.source.(@id==theId);
				if (userWithTheId.length() > 0) {
					XML(u).setChildren(userWithTheId[0].@name);
				}
			}
		}

	}
}