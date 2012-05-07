package cofm.command
{
	import cofm.event.*;
	import cofm.util.*;
	
	public class CreatePersonalViewCommand extends AbstractDurableCommand
	{
		private var _modelId: int;
		private var _pvName: String;
		private var _pvDes: String;
		
		public function CreatePersonalViewCommand(modelId: int, pvName: String, pvDes: String)
		{
			super();
			this._modelId = modelId;
			this._pvName = pvName;
			this._pvDes = pvDes;
		}
		
		override protected function createRequest():Object {
			return {
				"name": Cst.REQ_CREATE_PV,
				"modelId": this._modelId,
				"pvName": this._pvName,
				"pvDes": this._pvDes
			}
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_CREATE_PV == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				ClientEvtDispatcher.instance().dispatchEvent(
					new CreatePersonalViewEvent(CreatePersonalViewEvent.SUCCESS, 
						data));
			}
		}
	}
}