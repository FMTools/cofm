package cofm.command
{
	import cofm.util.*;
	import cofm.event.ClientEvent;
	
	public class AbstractDurableCommand implements IDurableCommand
	{
		protected var cmdId: int;
		
		public function AbstractDurableCommand()
		{
		}
		
		public function redo():void
		{
		}
		
		public function undo():void
		{
		}
		
		public function handleResponse(data:Object):void
		{
			CommandBuffer.instance().removeCommand(cmdId);
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]) {
				handleSuccess(data);
				
				ClientEvtDispatcher.instance().dispatchEvent(
					new ClientEvent(ClientEvent.COMMAND_EXECUTED, this.getId()));
				
			} else if (Cst.RSP_ERROR == data[Cst.FIELD_RSP_NAME]) {
				handleError(data);
			}
		}
		
		public function getId():int
		{
			return cmdId;
		}
		
		public function execute():void
		{
			var request: Object = createRequest();
			
			if (request != null) {
				cmdId = CommandBuffer.instance().addCommand(this);
				request.id = cmdId;
				Connector.instance().send(request);
			}
			
		}
		
		/*abstract*/ protected function createRequest(): Object { 
			throw new Error("Abstract Method.");		
		}
		
		/*abstract*/ protected function handleSuccess(data: Object): void {
			throw new Error("Abstract Method.");	
		}	
		
		/*abstract*/ protected function handleError(data: Object): void {
			throw new Error("Abstract Method.");
		}
	}
}