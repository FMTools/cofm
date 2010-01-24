package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.ModelCollection;
	import collab.fm.client.event.ClientEvent;
	import collab.fm.client.event.SearchModelEvent;
	import collab.fm.client.util.*;

	import flash.events.IEventDispatcher;

	public class ListModelCommand implements IDurableCommand {

		private var _target: IEventDispatcher;
		private var _cmdId: int;
		private var _searchWord: String;

		public function ListModelCommand(target: IEventDispatcher, word: String = null) {
			_target = target;
			_searchWord = word;
		}

		public function execute(): void {
			_cmdId = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					"id": _cmdId,
					"name": Cst.REQ_LIST_MODEL
				};

			if (_searchWord != null) {
				request.searchWord = _searchWord;
			}

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
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]
				&& Cst.REQ_LIST_MODEL == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_cmdId);

				// Change data
				if (_searchWord == null) {
					ModelCollection.instance.refresh(data["models"]);
					// Notify views
					_target.dispatchEvent(new ClientEvent(ClientEvent.LIST_MODEL_SUCCESS));
				} else {
					// Change others' list only
					trace("Resposne with search word: " + _searchWord);
					ModelCollection.instance.refresh({
							"event": Cst.DATA_OTHERS_MODEL,
							"models": data["models"],
							"searchWord": _searchWord
						}, true);
					_target.dispatchEvent( 
						new SearchModelEvent(SearchModelEvent.SUCCESS,
						_searchWord, ModelCollection.instance.lastSearchHits));
				}
			}
		}

	}
}