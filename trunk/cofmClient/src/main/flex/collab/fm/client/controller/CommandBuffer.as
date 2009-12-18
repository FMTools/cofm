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

		public function addCommandAt(index: int, cmd: DurableCommand): void {
			buffer[index] = cmd;
		}

		public function getCommandAt(index: int): Command {
			if (index < 0 || index >= buffer.length) {
				return null;
			}
			return buffer[index];
		}

		private function CommandBuffer() {
			nextId = 0;
			buffer = new Array();
		}

	}
}