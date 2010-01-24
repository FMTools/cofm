package collab.fm.client.command {
	import collab.fm.client.data.*;
	import collab.fm.client.event.ClientEvent;
	import collab.fm.client.util.*;

	import flash.events.IEventDispatcher;
	import flash.utils.Dictionary;

	public class ListUserCommand extends DatalessCommand {
		public function ListUserCommand(target:IEventDispatcher) {
			super(target, Cst.REQ_LIST_USER, false, false);
		}

		/**  ListUserResponse: see Server.ListUserResponse
		 * 		users: array of <id, name>
		 */
		override protected function refreshDataAndViews(data:Object): void {
			var list: Dictionary = new Dictionary();
			for each (var user: Object in(data["users"] as Array)) {
				list[user.id] = user.name;
			}
			var changes: Object = {
					"event": Cst.DATA_USER_NAMES,
					"list": list
				};
			ModelCollection.instance.refresh(changes, true);
			User.instance.refresh(changes, true);

			// No event dispatched now
			_target.dispatchEvent(new ClientEvent(ClientEvent.LIST_USER_SUCCESS));
		}

	}
}