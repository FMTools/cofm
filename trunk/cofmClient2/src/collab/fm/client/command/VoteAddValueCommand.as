package collab.fm.client.command
{
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	public class VoteAddValueCommand implements IDurableCommand
	{
		private var _attr: String;
		private var _val: String;
		private var _vote: Boolean;
		private var _id: int;
		private var _fid: int;
		
		public function VoteAddValueCommand(attr: String, val: String, fid: int, vote: Boolean = true)
		{
			_attr = attr;
			_val = val;
			_fid = fid;
			_vote = vote;
		}

		public function execute():void
		{
			_id = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_VALUE,
					requesterId: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					featureId: _fid,
					yes: _vote,
					attr: _attr,
					val: _val
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
				Cst.REQ_VA_VALUE == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
		
	}
}