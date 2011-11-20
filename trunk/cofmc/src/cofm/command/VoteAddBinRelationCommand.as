package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class VoteAddBinRelationCommand extends AbstractDurableCommand
	{
		private var _typeId: int;
		private var _left: int;
		private var _right: int;
		private var _relationId: int;
		private var _vote: Boolean;
		
		public function VoteAddBinRelationCommand(typeId: int, left: int, right: int, relationId: int=-1, vote: Boolean=true)
		{
			super();
			_typeId = typeId;
			_left = left;
			_right = right;
			_relationId = relationId;
			_vote = vote;
		}

		override protected function createRequest():Object {
			var request: Object = {
				name: Cst.REQ_VA_BIN_REL,
					requesterId: UserList.instance().myId,
					modelId: ModelCollection.instance().currentModelId,
					activePvId: PersonalViewManager.instance().active,
					yes: _vote,
					typeId: _typeId,
					sourceId: _left,
					targetId: _right
			};
			if (_relationId > 0) {
				request.relationId = _relationId;
			}
			return request;
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_VA_BIN_REL == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}