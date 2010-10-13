package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class StartEditFeatureCommand implements ICommand
	{
		private var featureId: int;
		public function StartEditFeatureCommand(featureId: int)
		{
			this.featureId = featureId;
		}

		public function execute():void
		{
			var request: Object = {
				name: Cst.REQ_FOCUS,
				requesterId: UserList.instance().myId,
				modelId: ModelCollection.instance().currentModelId,
				featureId: this.featureId
			};
			Connector.instance().send(JsonUtil.objectToJson(request));
		}
		
	}
}