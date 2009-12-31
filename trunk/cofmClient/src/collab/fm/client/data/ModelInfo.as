package collab.fm.client.data {

	import collab.fm.client.util.Cst;

	import mx.collections.IViewCursor;
	import mx.collections.XMLListCollection;

	public class ModelInfo extends AbstractDataView {

		private var id: int;

		[Bindable]
		public var userNum: int;

		[Bindable]
		public var name: String;

		[Bindable]
		public var des: String;

		[Bindable]
		public var users: XMLListCollection;

		public function ModelInfo(modelId: int) {
			super();
			ModelCollection.instance.registerSubDataView(this);
			id = modelId;
			updateEntireData(null);
		}

		override protected function updateEntireData(input:Object): void {
			var me: XMLList = ModelCollection(parent).my.source.(@id == this.id);
			if (me.length() <= 0) {
				me = ModelCollection(parent).others.source.(@id == this.id);
			}
			if (me.length() > 0) {
				userNum = XML(me).@userNum;
				name = XML(me).@name;
				des = XML(me).des;
				users = new XMLListCollection(XML(me).users.user);
			}
		}

		override protected function updateMinorChange(input:Object): void {
			if (input.event == Cst.DATA_USER_NAMES) {
				// We must rebuild users, otherwise the Repeater in ModelInfoRender will not update views 
				// because the length of users is unchanged.
				updateEntireData(null);
			}
		}
	}
}