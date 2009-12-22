package collab.fm.client.command
{
	public class ListUserCommand implements DurableCommand
	{
		public function ListUserCommand()
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