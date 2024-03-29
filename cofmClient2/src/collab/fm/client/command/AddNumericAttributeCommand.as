package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class AddNumericAttributeCommand extends AddAttributeCommand {
		private var _min: Number;
		private var _max: Number;
		private var _unit: String;

		public function AddNumericAttributeCommand(name: String, min: Number, max: Number, unit: String, multi: Boolean=true, dup: Boolean=true) {
			super(name, Cst.ATTR_TYPE_NUMBER, multi, dup);
			_min = min;
			_max = max;
			_unit = unit;
		}

		override public function execute(): void {
			_id = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_ATTR_NUMBER,
					requesterId: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					attr: _name,
					type: _type,
					multiYes: _multi,
					allowDup: _dup,
					min: _min,
					max: _max,
					unit: _unit
				};
			Connector.instance.send(JsonUtil.objectToJson(request));
		}

		override public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME] &&
				Cst.REQ_VA_ATTR_NUMBER == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}
	}
}