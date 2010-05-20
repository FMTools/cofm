package collab.fm.client.command
{
	import collab.fm.client.util.*;
	import collab.fm.client.data.*;
	import collab.fm.client.cmn.*;
	
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
				name: Cst.REQ_EDIT,
				requesterId: UserList.instance.myId,
				modelId: ModelCollection.instance.currentModelId,
				featureId: this.featureId
			};
			Connector.instance.send(JsonUtil.objectToJson(request));
		}
		
	}
}