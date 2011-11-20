package cofm.command 
{
	import cofm.event.*;
	import cofm.model.*;
	import cofm.util.*;
	
	import mx.utils.StringUtil;
	
	public class VoteAddFeatureCommand extends AbstractDurableCommand
	{
		private var _typeId: int;
		private var _entityId: int;
		private var _vote: Boolean;
		public function VoteAddFeatureCommand(typeId: int, entityId: int = -1, vote: Boolean=true)
		{
			super();
			_typeId = typeId;
			_entityId = entityId;
			_vote = vote;
		}

		override protected function createRequest():Object {
			var request: Object = {
				name: Cst.REQ_VA_ENTITY,
					requesterId: UserList.instance().myId,
					modelId: ModelCollection.instance().currentModelId,
					activePvId: PersonalViewManager.instance().active,
					yes: _vote,
					typeId: _typeId
			};
			if (_entityId > 0) {
				request.entityId = _entityId;
			}
			return request;
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_VA_ENTITY == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}