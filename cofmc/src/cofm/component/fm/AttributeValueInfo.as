package cofm.component.fm
{
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	
	[Bindable]
	public class AttributeValueInfo extends EventDispatcher
	{
		public var valueIndex: int;
		public var attributeId: int;
		public var attributeName: String;
		public var attributeType: String;
		public var value: String;
		public var numSupporters: int;
		public var numOpponents: int;
		public var myVote: int;
		
		public static const VOTE_NULL: int = 0;
		public static const VOTE_YES: int = 1;
		public static const VOTE_NO: int = 2;
		
		public function AttributeValueInfo(target:IEventDispatcher=null)
		{
			super(target);
		}
	}
}