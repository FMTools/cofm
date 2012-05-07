package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class AddAttributeCommand extends AbstractDurableCommand {
		protected var _name: String;
		protected var _type: String;
		protected var _multi: Boolean;
		protected var _dup: Boolean;
		protected var _attrId: int;
		protected var _modelId: int;
		protected var _entypeId: int;

		public function AddAttributeCommand(
			name: String, type: String,
			entypeId: int,
			multi: Boolean=true, dup: Boolean=true,
			modelId: int = -1, attrId: int = -1) {
			super();
			_name = name;
			_type = type;
			_multi = multi;
			_dup = dup;
			_attrId = attrId;
			_modelId = modelId;
			_entypeId = entypeId;
		}
		 
		override protected function createRequest(): Object {
			var request: Object = {
					name: Cst.REQ_VA_ATTR,
					requesterId: UserList.instance().myId,
					modelId: (_modelId < 0) ? ModelCollection.instance().currentModelId : _modelId,
					attr: _name,
					type: _type,
					multiYes: _multi,
					allowDup: _dup,
					entityTypeId: _entypeId
				};
			if (_attrId > 0) {
				request.attrId = _attrId;
			}
			return request;
		}

		override protected function handleSuccess(data:Object): void {
			if (Cst.REQ_VA_ATTR == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}

	}
}