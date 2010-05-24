package collab.fm.client.command
{	
	import collab.fm.client.util.*;
	import collab.fm.client.data.*;
	import collab.fm.client.cmn.*;
	
	public class ExitModelCommand implements ICommand
	{
		private var _model: int;
		public function ExitModelCommand(modelId: int)
		{
			_model = modelId;
		}

		public function execute():void
		{
			var request: Object = {
				name: Cst.REQ_EXIT_MODEL,
				requesterId: UserList.instance.myId,
				modelId: _model
			};
			Connector.instance.send(JsonUtil.objectToJson(request));
		}
		
	}
}