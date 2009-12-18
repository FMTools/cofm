package collab.fm.client.command
{
	public class RegisterCommand implements DurableCommand
	{
		public function RegisterCommand()
		{
		}

		public function execute():Boolean
		{
			return false;
		}
		
		public function redo():Boolean
		{
			return false;
		}
		
		public function undo():Boolean
		{
			return false;
		}
		
	}
}