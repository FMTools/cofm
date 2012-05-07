package cofm.command
{
	import cofm.event.*;
	import cofm.model.*;
	import cofm.util.*;
	
	public class EditAddBinRelationTypeCommand extends AbstractDurableCommand
	{
		
		private var _typeName: String;
		private var _relId: int;
		private var _modelId: int;
		private var _sourceId: int;
		private var _targetId: int;
		private var _hier: Boolean;
		private var _dir: Boolean;
		
		public function EditAddBinRelationTypeCommand(
			typeName: String, sourceId: int, targetId: int,
			hier: Boolean, dir: Boolean, modelId: int = -1,
			relId: int = -1)
		{
			super();
			_typeName = typeName;
			_sourceId = sourceId;
			_targetId = targetId;
			_hier = hier;
			_dir = dir;
			_relId = relId;
			_modelId = modelId;
		}
		
		override protected function createRequest():Object {
			var request: Object = {
				name: Cst.REQ_EA_BINREL_TYPE,
					requesterId: UserList.instance().myId,
					modelId: (_modelId < 0) ? ModelCollection.instance().currentModelId : _modelId,
					typeName: _typeName,
					sourceId: _sourceId,
					targetId: _targetId,
					hierarchical: _hier,
					directed: _dir
			};
			if (_relId > 0) {
				request.relId = _relId;
			}
			return request;
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_EA_BINREL_TYPE == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}