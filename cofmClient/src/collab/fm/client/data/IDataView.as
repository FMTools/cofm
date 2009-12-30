package collab.fm.client.data {

	public interface IDataView {
		function refresh(input: Object, minorChange: Boolean = false): void;
		function registerSubDataView(dv: IDataView): void;
		function set parent(dv: IDataView): void;
	}
}