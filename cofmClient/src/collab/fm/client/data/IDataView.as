package collab.fm.client.data
{
	public interface IDataView
	{
		public function refresh(minorChange: Object): void;
		public function registerSubDataView(dv: IDataView): void;
	}
}