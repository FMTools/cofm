package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class CreateFeatureCommand extends CommitOperationCommand {
		private var _name: String;

		public function CreateFeatureCommand(name: String) {
			super();
			_name = name;
		}

		override protected function makeOperation(): Object {
			return {
					name: Cst.OP_CREATE_FEATURE,
					vote: true,
					userid: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					value: _name  
				};
		}
	}
}