package collab.fm.client.command
{
	public class LogoutCommand implements DurableCommand
	{
		public function LogoutCommand()
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