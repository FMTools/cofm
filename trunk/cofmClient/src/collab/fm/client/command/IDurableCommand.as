package collab.fm.client.command
{
	public interface IDurableCommand extends ICommand
	{
		public function redo(): Boolean;
		public function undo(): Boolean;
		public function setDurable(val: Boolean): void;
		public function handleResponse(data: Object): Boolean;
	}
}