package collab.fm.client.data {

	public class AbstractDataView implements IDataView {
		private var _parent: IDataView;
		private var subViews: Array = new Array();

		public function AbstractDataView() {
			//IMPORTANT: insert initial empty data to avoid null pointer exceptions
		}

		public function refresh(input: Object, minorChange: Boolean=false): void {
			if (minorChange == true) {
				updateMinorChange(input);
			} else {
				updateEntireData(input);
			}
			for each (var v: Object in subViews) {
				(v as IDataView).refresh(input, minorChange);
			}
		}

		protected function updateMinorChange(input: Object): void {
		}

		protected function updateEntireData(input: Object): void {
		}

		public function registerSubDataView(dv: IDataView): void {
			subViews.push(dv);
			dv.parent = this;
		}

		public function get parent(): IDataView {
			return _parent;
		}

		public function set parent(dv: IDataView): void {
			_parent = dv;
		}

	}
}