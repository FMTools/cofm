package collab.fm.client.command
{
	public class AddNameCommand implements DurableCommand
	{
		public function AddNameCommand()
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