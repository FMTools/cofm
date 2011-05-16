package cofm.command 
{
	import cofm.event.*;
	import cofm.model.*;
	import cofm.util.*;
	
	import mx.utils.StringUtil;
	
	public class VoteAddFeatureCommand implements IDurableCommand
	{
		private var _id: int;
		private var _typeId: int;
		private var _entityId: int;
		private var _vote: Boolean;
		public function VoteAddFeatureCommand(typeId: int, entityId: int = -1, vote: Boolean=true)
		{
			_typeId = typeId;
			_entityId = entityId;
			_vote = vote;
		}

		public function execute():void
		{
			_id = CommandBuffer.instance().addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_ENTITY,
					requesterId: UserList.instance().myId,
					modelId: ModelCollection.instance().currentModelId,
					yes: _vote,
					typeId: _typeId
				};
			if (_entityId > 0) {
				request.entityId = _entityId;
			}
			Connector.instance().send(request);
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
				Cst.REQ_VA_ENTITY == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance().removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}