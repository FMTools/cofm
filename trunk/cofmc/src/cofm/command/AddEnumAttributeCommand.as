package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class AddEnumAttributeCommand extends AddAttributeCommand {
		private var _enums: Array;

		public function AddEnumAttributeCommand(
			name: String, enums: Array, 
			entypeId: int,
			multi: Boolean=true, dup: Boolean=true,
			modelId: int = -1, attrId: int = -1) {
			super(name, Cst.ATTR_TYPE_ENUM, entypeId, multi, dup, modelId, attrId);
			_enums = enums;
		}

		override protected function createRequest():Object {
			var request: Object = {
					name: Cst.REQ_VA_ATTR_ENUM,
					requesterId: UserList.instance().myId,
					modelId: (_modelId < 0) ? ModelCollection.instance().currentModelId : _modelId,
					attr: _name,
					type: _type,
					multiYes: _multi,
					allowDup: _dup,
					entityTypeId: _entypeId,
					vlist: _enums
				};
			if (_attrId > 0) {
				request.attrId = _attrId;
			}
			return request;
		}

		override protected function handleSuccess(data:Object):void {
			if (Cst.REQ_VA_ATTR_ENUM == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}

	}
}