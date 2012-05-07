package cofm.command.composite
{
	import cofm.command.IDurableCommand;
	import cofm.event.ClientEvent;
	import cofm.util.*;
	
	public class AbstractCompositeCommand implements IDurableCommand
	{
		protected var queue: Array;
		protected var last: IDurableCommand;
		
		public function AbstractCompositeCommand()
		{
			queue = new Array();
			ClientEvtDispatcher.instance().addEventListener(ClientEvent.COMMAND_EXECUTED, onCommandExecuted);
		}
		
		public function redo():void
		{
		}
		
		public function undo():void
		{
		}
		
		public function addCommand(cmd: IDurableCommand): void {
			queue.push(cmd);
		}
		
		public function handleResponse(data:Object):void
		{
		}
		
		public function getId():int
		{
			return -1;
		}
		
		public function execute():void
		{
			executeNext();
		}
		
		protected function onCommandExecuted(evt: ClientEvent): void {
			if (last.getId() == int(evt.data)) {
				executeNext();
			}
		}
		
		protected function executeNext(): void {
			if (queue.length <= 0) {
				ClientEvtDispatcher.instance().removeEventListener(ClientEvent.COMMAND_EXECUTED, onCommandExecuted);
				return;
			}
			
			last = queue.shift();
			
			preExecution();
			last.execute();
			postExecution();
		}
		
		protected function preExecution(): void {
			throw new Error("Abstract Method");
		}
		
		protected function postExecution(): void {
			throw new Error("Abstract Method");
		}
	}
}