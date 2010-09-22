package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;

	public class AddEnumAttributeCommand extends AddAttributeCommand {
		private var _enums: Array;

		public function AddEnumAttributeCommand(name: String, enums: Array, multi: Boolean=true, dup: Boolean=true) {
			super(name, Cst.ATTR_TYPE_ENUM, multi, dup);
			_enums = enums;
		}

		override public function execute(): void {
			_id = CommandBuffer.instance.addCommand(this);
			var request: Object = {
					id: _id,
					name: Cst.REQ_VA_ATTR_ENUM,
					requesterId: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					attr: _name,
					type: _type,
					multiYes: _multi,
					allowDup: _dup,
					vlist: _enums
				};
			Connector.instance.send(JsonUtil.objectToJson(request));
		}

		override public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == data[Cst.FIELD_RSP_NAME] &&
				Cst.REQ_VA_ATTR_ENUM == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance.removeCommand(_id);

				ClientEvtDispatcher.instance().dispatchEvent(
					new OperationCommitEvent(OperationCommitEvent.COMMIT_SUCCESS, data));
			}
		}

	}
}