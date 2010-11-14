package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class AddNumericAttributeCommand extends AddAttributeCommand {
		private var _min: Number;
		private var _max: Number;
		private var _unit: String;

		public function AddNumericAttributeCommand(
			name: String, min: Number, max: Number, unit: String, 
			entypeId: int,
			multi: Boolean=true, dup: Boolean=true,
			modelId: int = -1, attrId: int = -1) {
			
			super(name, Cst.ATTR_TYPE_ENUM, entypeId, multi, dup, modelId, attrId);
			_min = min;
			_max = max;
			_unit = unit;
		}

		override public function execute(): void {
			_id = CommandBuffer.instance().addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_ATTR_NUMBER,
					requesterId: UserList.instance().myId,
					modelId: (_modelId < 0) ? ModelCollection.instance().currentModelId : _modelId,
					attr: _name,
					type: _type,
					multiYes: _multi,
					allowDup: _dup,
					entityTypeId: _entypeId,
					min: _min,
					max: _max,
					unit: _unit
				};
			if (_attrId > 0) {
				request.attrId = _attrId;
			}
			Connector.instance().send(JsonUtil.objectToJson(request));
		}

		override public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME] &&
				Cst.REQ_VA_ATTR_NUMBER == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance().removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
	}
}