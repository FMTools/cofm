package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class VoteAddBinRelationCommand implements IDurableCommand
	{
		private var _type: String;
		private var _left: int;
		private var _right: int;
		private var _relationId: int;
		private var _vote: Boolean;
		private var _id: int;
		
		public function VoteAddBinRelationCommand(type: String, left: int, right: int, relationId: int=-1, vote: Boolean=true)
		{
			_type = type;
			_left = left;
			_right = right;
			_relationId = relationId;
			_vote = vote;
		}

		public function execute():void
		{
			_id = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_BIN_REL,
					requesterId: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					yes: _vote,
					type: _type,
					leftFeatureId: _left,
					rightFeatureId: _right
				};
			if (_relationId > 0) {
				request.relationshipId = _relationId;
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
				Cst.REQ_VA_BIN_REL == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}