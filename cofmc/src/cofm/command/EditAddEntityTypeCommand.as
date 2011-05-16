package cofm.command
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class EditAddEntityTypeCommand implements IDurableCommand
	{
		private var _id: int;
		
		public var _typeName: String;
		public var _superId: int;
		public var _typeId: int;
		public var _modelId: int;
		
		public var _handle: int;
		
		public function EditAddEntityTypeCommand(typeName: String, superId: int = -1, modelId: int = -1, typeId: int = -1, handle: int = -1)
		{
			_typeName = typeName;
			_superId = superId;
			_typeId = typeId;
			_modelId = modelId;
			
			_handle = handle;
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
				Cst.REQ_EA_ENTITY_TYPE == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				CommandBuffer.instance().removeCommand(_id);
				
				data[Cst.FIELD_CMD_HANDLE] = _handle;
				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
		public function execute():void
		{
			_id = CommandBuffer.instance().addCommand(this);
			var request: Object = {
				id: _id,
				name: Cst.REQ_EA_ENTITY_TYPE,
				requesterId: UserList.instance().myId,
				modelId: (_modelId < 0) ? ModelCollection.instance().currentModelId : _modelId,
				typeName: _typeName,
				superTypeId: _superId
			};
			if (_typeId > 0) {
				request.typeId = _typeId;
			}
			Connector.instance().send(request);
		}
	}
}