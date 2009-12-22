package collab.fm.client.command
{
	public class UpdateCommand implements DurableCommand
	{
		public function UpdateCommand()
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