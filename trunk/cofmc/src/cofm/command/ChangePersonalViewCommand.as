package cofm.command
{
	import cofm.event.*;
	import cofm.util.*;
	
	public class ChangePersonalViewCommand extends AbstractDurableCommand
	{
		private var _modelId: int;
		private var _pvId: int;
		
		public function ChangePersonalViewCommand(modelId: int, pvId: int)
		{
			super();
			this._modelId = modelId;
			this._pvId = pvId;
		}
		
		override protected function createRequest():Object {
			return {
				"name": Cst.REQ_CHANGE_PV,
				"modelId": this._modelId,
				"pvId": this._pvId
			};
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_CHANGE_PV == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				ClientEvtDispatcher.instance().dispatchEvent(
					new PersonalViewUpdateEvent(PersonalViewUpdateEvent.SUCCESS, data));
			}
		}
	}
}