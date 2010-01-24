package collab.fm.client.data {

	import collab.fm.client.util.Cst;

	import mx.collections.XMLListCollection;
	import mx.core.UIComponent;
	import mx.core.IFlexDisplayObject;

	public class ModelInfo extends AbstractDataView {

		[Bindable]
		public var id: int;

		[Bindable]
		public var userNum: int;

		[Bindable]
		public var name: String;

		[Bindable]
		public var des: String;

		[Bindable]
		public var users: XMLListCollection;

		private var binder: UIComponent;

		public function ModelInfo(modelId: int, binder: UIComponent) {
			super();
			id = modelId;
			this.binder = binder;
			ModelCollection.instance.registerSubDataView(this);
			updateEntireData(null);
		}

		override protected function updateEntireData(input:Object): void {
			var me: XMLList = ModelCollection(parent).others.source.(@id == this.id);
			if (me.length() > 0) {
				userNum = XML(me).@userNum;
				name = XML(me).@name;
				des = XML(me).des;
				users = new XMLListCollection(XML(me).users.user);
				binder.enabled = true;
			} else {
				binder.deleteReferenceOnParentDocument(IFlexDisplayObject(binder.parentDocument));
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