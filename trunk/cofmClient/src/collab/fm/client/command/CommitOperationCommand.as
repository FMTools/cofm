package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class CommitOperationCommand implements IDurableCommand {
		private var _cmdId: int;

		public function CommitOperationCommand() {
		}

		public function execute(): void {
			_cmdId = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					id: _cmdId,
					name: Cst.REQ_COMMIT,
					requesterId: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					operation: makeOperation()
				};
			Connector.instance.send(JsonUtil.objectToJson(request));
		}

		/*abstract*/
		protected function makeOperation(): Object {
			return null;
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
		}

		public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME] &&
				Cst.REQ_COMMIT == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_cmdId);
				

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data["operations"] as Array));
			}
		}

	}
}