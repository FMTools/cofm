package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class AddAttributeCommand implements IDurableCommand {
		protected var _id: int;
		
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
			_name = name;
			_type = type;
			_multi = multi;
			_dup = dup;
			_attrId = attrId;
			_modelId = modelId;
			_entypeId = entypeId;
		}

		public function execute(): void {
			_id = CommandBuffer.instance().addCommand(this);
			var request: Object = {
					id: _id,
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
			Connector.instance().send(JsonUtil.objectToJson(request));
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
		}

		public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME] &&
				Cst.REQ_VA_ATTR == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance().removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}

	}
}