package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	import mx.utils.StringUtil;
	
	public class CreateOrVoteNameCommand extends CommitOperationCommand {

		private var _name: String;
		private var _vote: Boolean;
		private var _feature: int;

		public function CreateOrVoteNameCommand(feature: int, name: String, vote: Boolean=true) {
			super();
			_feature = feature;
			_name = mx.utils.StringUtil.trim(name);
			_vote = vote;
		}

		override protected function makeOperation(): Object {
			return {
					name: Cst.OP_ADD_NAME,
					vote: _vote,
					userid: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					featureId: _feature,
					value: _name  
				};
		}
	}
}