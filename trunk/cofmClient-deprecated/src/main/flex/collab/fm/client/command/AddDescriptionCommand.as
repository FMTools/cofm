package collab.fm.client.command
{
	public class AddDescriptionCommand implements DurableCommand
	{
		public function AddDescriptionCommand()
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