package collab.fm.client.command
{
	public class SetOptionalityCommand implements DurableCommand
	{
		public function SetOptionalityCommand()
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