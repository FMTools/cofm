package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class AddCommentCommand extends AbstractDurableCommand
	{
		private var _feature: int;
		private var _content: String;
		
		public function AddCommentCommand(feature: int, content: String)
		{
			super();
			this._feature = feature;
			this._content = content;
		}

		override protected function createRequest():Object
		{
			if (_feature <= 0) {
				return null;
			}
			return {
				"name": Cst.REQ_COMMENT,
				"requesterId": UserList.instance().myId,
				"entityId": _feature,
				"content": _content
			};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_COMMENT == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				ClientEvtDispatcher.instance().dispatchEvent(
					new AddCommentEvent(AddCommentEvent.SUCCESS,
					int(data[Cst.FIELD_RSP_SOURCE_USER_ID]),
					int(data["entityId"]),
					String(data["content"]),
					String(data["execTime"])));
			}
		}
		
	}
}