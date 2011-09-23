package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class ListModelCommand extends AbstractDurableCommand {

		private var _searchWord: String;

		public function ListModelCommand(word: String = null) {
			super();
			_searchWord = word;
		}

		override protected function createRequest():Object {
			var request: Object = {
				"name": Cst.REQ_LIST_MODEL
			};
			
			if (_searchWord != null) {
				request.searchWord = _searchWord;
			}
			
			return request;
		}
		
		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_LIST_MODEL == data[Cst.FIELD_RSP_SOURCE_NAME]) {
				
				ClientEvtDispatcher.instance().dispatchEvent(
					new ModelSearchEvent(ModelSearchEvent.SUCCESS,
						_searchWord, 
						data["models"] as Array, 
						data["exactlyMatches"] as Boolean));
			}
		}

	}
}