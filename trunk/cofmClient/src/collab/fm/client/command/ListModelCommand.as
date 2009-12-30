package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.ModelCollection;
	import collab.fm.client.event.ClientEvent;
	import collab.fm.client.util.*;

	import flash.events.EventDispatcher;

	public class ListModelCommand extends EventDispatcher implements IDurableCommand {
		private var cmdId: int;

		public function ListModelCommand() {
			super();
		}

		public function execute(): void {
			cmdId = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					"id": cmdId,
					"name": Cst.REQ_LIST_MODEL
				};
			Connector.instance.send(JsonUtil.objectToJson(request));
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
			throw new Error("Unsupported Operation Error.");
		}

		/** Response format: (see server.ListModelResponse for details)
		 *      models: array of models.
		 */
		public function handleResponse(data:Object): void {
			trace("List Model Response received.");
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]
				&& Cst.REQ_LIST_MODEL == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				// Change data
				ModelCollection.instance.refresh(data["models"]);

				// Notify views
				this.dispatchEvent(new ClientEvent(ClientEvent.LIST_MODEL_SUCCESS));

				CommandBuffer.instance.removeCommand(cmdId);
			}
		}

	}
}