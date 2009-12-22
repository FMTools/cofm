package collab.fm.client.command
{
	public class LoginCommand implements DurableCommand
	{
		public function LoginCommand()
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