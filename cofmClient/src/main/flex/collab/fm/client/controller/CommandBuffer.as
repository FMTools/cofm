package collab.fm.client.controller {
	import collab.fm.client.command.DurableCommand;


	public class CommandBuffer {

		private static var cb: CommandBuffer = new CommandBuffer();

		private var nextId: int;
		private var buffer: Array;

		public static function getInstance(): CommandBuffer {
			return cb;
		}

		public function getNextId(): int {
			// TODO: compact the array when possible
			return nextId++;
		}

		public function addCommant(id: int, cmd: DurableCommand): void {
			buffer[id] = cmd;
		}

		public function getCommand(id: int): Command {
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