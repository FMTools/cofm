package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class VoteAddValueCommand implements IDurableCommand
	{
		private var _attrId: int;
		private var _val: String;
		private var _vote: Boolean;
		private var _id: int;
		private var _entityId: int;
		
		public function VoteAddValueCommand(attrId: int, val: String, entityId: int, vote: Boolean = true)
		{
			_attrId = attrId;
			_val = val;
			_entityId = entityId;
			_vote = vote;
		}

		public function execute():void
		{
			_id = CommandBuffer.instance().addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_VALUE,
					requesterId: UserList.instance().myId,
					modelId: ModelCollection.instance().currentModelId,
					yes: _vote,
					val: _val,
					attrId: _attrId,
					entityId: _entityId
				};
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
				Cst.REQ_VA_VALUE == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance().removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}