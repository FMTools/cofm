package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class AddCommentCommand implements IDurableCommand
	{
		private var _cmdId: int;
		private var _feature: int;
		private var _content: String;
		
		public function AddCommentCommand(feature: int, content: String)
		{
			this._feature = feature;
			this._content = content;
		}

		public function execute():void
		{
			if (_feature <= 0) {
				return;
			}
			_cmdId = CommandBuffer.instance.addCommand(this);
			var r: Object = {
				"id": _cmdId,
				"name": Cst.REQ_COMMENT,
				"requesterId": UserList.instance.myId,
				"featureId": _feature,
				"content": _content
			};
			Connector.instance.send(JsonUtil.objectToJson(r));
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
				Cst.REQ_COMMENT == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_cmdId);
				

				ClientEvtDispatcher.instance().dispatchEvent(
					new AddCommentEvent(AddCommentEvent.SUCCESS,
					int(data[Cst.FIELD_RSP_SOURCE_USER_ID]),
					int(data["featureId"]),
					String(data["content"]),
					String(data["dateTime"])));
			}
		}
		
	}
}