package collab.fm.client.command
{
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	import mx.utils.StringUtil;
	
	public class VoteAddFeatureCommand implements IDurableCommand
	{
		private var _id: int;
		private var _name: String;
		private var _feature: int;
		private var _vote: Boolean;
		public function VoteAddFeatureCommand(name: String, feature: int=-1, vote: Boolean=true)
		{
			_name = StringUtil.trim(name);
			_feature = feature;
			_vote = vote;
		}

		public function execute():void
		{
			_id = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_FEATURE,
					requesterId: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					yes: _vote,
					featureName: _name
				};
			if (_feature > 0) {
				request.featureId = _feature;
			}
			Connector.instance.send(JsonUtil.objectToJson(request));
		}
		
		public function redo():void
		{
		}
		
		public function undo():void
		{
		}
		
		public function setDurable(val:Boolean):void
		{
		}
		
		public function handleResponse(data:Object):void
		{
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME] &&
				Cst.REQ_VA_FEATURE == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}