package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	import mx.utils.StringUtil;
	public class CreateModelCommand extends AbstractDurableCommand {
		private var _name: String;
		private var _des: String;

		public function CreateModelCommand(name: String, des: String) {
			super();
			_name = mx.utils.StringUtil.trim(name);
			_des = mx.utils.StringUtil.trim(des);
		}

		override protected function createRequest():Object {
			return {
				"name": Cst.REQ_CREATE_MODEL,
					"requesterId": UserList.instance().myId,
					"modelName": _name,
					"description": _des
			};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_CREATE_MODEL == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				var theModel: XML = 
					<model isMine="true" id={data.modelId} name={_name} userNum="1">
						<des>{_des}</des>
						<users>
							<user>{UserList.instance().myId}</user>
						</users>
					</model>;
				// Model selection happens automatically after model creation.
				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelCreateEvent(ModelCreateEvent.SUCCESS, theModel));
			}
		}

	}
}