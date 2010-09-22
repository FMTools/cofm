package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	import flash.utils.Dictionary;

	public class ListUserCommand extends DatalessCommand {
		public function ListUserCommand() {
			super(Cst.REQ_LIST_USER, false, false);
		}

		/**  ListUserResponse: see Server.ListUserResponse
		 * 		users: array of <id, name>
		 */
		override protected function refreshDataAndViews(data:Object): void {
			var list: Dictionary = new Dictionary();
			for each (var user: Object in(data["users"] as Array)) {
				list[user.id] = user.name;
			}

			ClientEvtDispatcher.instance().dispatchEvent(
				new ListUserEvent(ListUserEvent.SUCCESS, list));

		}

	}
}