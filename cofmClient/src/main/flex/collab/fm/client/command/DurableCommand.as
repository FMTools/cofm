package collab.fm.client.command {

	public interface DurableCommand extends Command {
		public function redo(): Boolean;
		public function undo(): Boolean;
	}
}