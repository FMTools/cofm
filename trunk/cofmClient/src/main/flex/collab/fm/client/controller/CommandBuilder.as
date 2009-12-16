package collab.fm.client.controller {
	import collab.fm.client.command.Command;

	import flash.utils.Dictionary;


	public class CommandBuilder {

		private final static var pkg: String = "collab.fm.client.command";
		private final static var forwardedPrefix: String = "forwarded";

		private var commandMap: Dictionary = new Dictionary();

		private static var instance: CommandBuilder = new CommandBuilder();

		public static function get(): CommandBuilder {
			return instance;
		}

		public function buildCommand(info: Object): Command {
			//1. get "name" or "requestName" from info
			//2. if name=="forward" then get "requestName" from info (See Response.java for details)
			//3. get command class name from dictionary
			//4. new command(info);

			//When a command is sent by client UI, the command records itself into a list.
			//After the command is responsed (success, error or stale), it will be removed 
			//from the list (success/error), or manually/automatically redo (stale) the same
			//command.
			//If the command is forwarded by other clients, which means no record in this client, 
			//the command will be executed immediately.
			//- How to distinguish these 3 types of commands?
			//  - Initiated by UI: name = Request_Name
			//  - Responsed from server: name = success, error or stale
			//  - Forwarded from other clients: name = forward
		}

		private function CommandBuilder() {
			initCommandMap();
		}

		private function initCommandMap(): void {

		}

	}
}