package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class UpdateCommand extends AbstractDurableCommand {
		private var _modelId: int;

		public function UpdateCommand(modelId: int) {
			super();
			_modelId = modelId;
		}

		override protected function createRequest():Object {
			return {
				"name": Cst.REQ_UPDATE,
					"requesterId": UserList.instance().myId,
					"modelId": _modelId
			};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_UPDATE == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelUpdateEvent(ModelUpdateEvent.SUCCESS, data));
			}
		}
		
	}
}