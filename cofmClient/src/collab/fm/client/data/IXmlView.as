package collab.fm.client.data
{
	import mx.collections.XMLListCollection;
	
	public interface IXmlView extends IDataView
	{
		[Bindable]
		public function get asXml(): XMLListCollection;
		
		public function set asXml(xml: XMLListCollection): void;
	}
}