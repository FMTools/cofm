package collab.fm.client.command
{
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	public class AddAttributeCommand implements IDurableCommand
	{
		private var _id: int;
		private var _name: String;
		private var _type: String;
		private var _multi: Boolean;
		private var _dup: Boolean;
		private var _fid: int;
		
		public function AddAttributeCommand(name: String, type: String, fid: int, multi: Boolean=true, dup: Boolean=true)
		{
			_name = name;
			_type = type;
			_multi = multi;
			_dup = dup;
			_fid = fid;
		}

		public function execute():void
		{
			_id = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_ATTR,
					requesterId: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					featureId: _fid,
					attr: _name,
					type: _type,
					multiYes: _multi,
					allowDup: _dup
				};
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
				Cst.REQ_VA_ATTR == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}