package cofm.component.home
{
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	
	import mx.collections.XMLListCollection;
	
	/**
	 * Data model for ModelBrowser.List.ItemRenderer.
	 */
	[Bindable]
	public class ModelInfo extends EventDispatcher
	{
		public var id: int;
		public var name: String;
		public var users: XMLListCollection;
		public var description: String;
		
		public function ModelInfo(target:IEventDispatcher=null)
		{
			super(target);
		}
	}
}