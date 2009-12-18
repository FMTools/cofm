package collab.fm.client.command
{
	public class CreateFeatureCommand implements DurableCommand
	{
		public function CreateFeatureCommand()
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