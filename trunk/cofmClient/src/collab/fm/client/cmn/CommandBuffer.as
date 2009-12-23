package collab.fm.client.cmn
{
	import collab.fm.client.command.IDurableCommand;
	
	public class CommandBuffer {

		private static var cb: CommandBuffer = new CommandBuffer();

		private var nextId: int;
		private var buffer: Array;

		public static function get instance(): CommandBuffer {
			return cb;
		}

		public function addCommand(cmd: IDurableCommand): int {
			buffer[nextId] = cmd;
			return nextId++;
		}

		public function getCommand(id: int): IDurableCommand {
			if (id < 0 || id >= buffer.length) {
				return null;
			}
			return buffer[id];
		}

		private function CommandBuffer() {
			nextId = 0;
			buffer = new Array();
		}

	}
}