package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class ListModelCommand implements IDurableCommand {

		private var _cmdId: int;
		private var _searchWord: String;

		public function ListModelCommand(word: String = null) {
			_searchWord = word;
		}

		public function execute(): void {
			_cmdId = CommandBuffer.instance().addCommand(this);
			var request: Object = {
					"id": _cmdId,
					"name": Cst.REQ_LIST_MODEL
				};

			if (_searchWord != null) {
				request.searchWord = _searchWord;
			}

			Connector.instance().send(request);
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
		 * 		exactlyMatches: boolean
		 */
		public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME]
				&& Cst.REQ_LIST_MODEL == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance().removeCommand(_cmdId);

				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelSearchEvent(ModelSearchEvent.SUCCESS,
					_searchWord, 
					data["models"] as Array, 
					data["exactlyMatches"] as Boolean));
			}
		}

	}
}