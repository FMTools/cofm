package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class VoteAddValueCommand extends AbstractDurableCommand
	{
		private var _attrId: int;
		private var _val: String;
		private var _vote: Boolean;
		private var _entityId: int;
		
		public function VoteAddValueCommand(attrId: int, val: String, entityId: int, vote: Boolean = true)
		{
			super();
			_attrId = attrId;
			_val = val;
			_entityId = entityId;
			_vote = vote;
		}

		override protected function createRequest():Object {
			return {
				name: Cst.REQ_VA_VALUE,
					requesterId: UserList.instance().myId,
					modelId: ModelCollection.instance().currentModelId,
					yes: _vote,
					val: _val,
					attrId: _attrId,
					entityId: _entityId
			};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_VA_VALUE == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
			
	}
}