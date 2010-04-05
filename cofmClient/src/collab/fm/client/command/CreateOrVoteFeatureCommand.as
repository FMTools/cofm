package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class CreateOrVoteFeatureCommand extends CommitOperationCommand {
		private var _name: String;
		private var _feature: int;
		private var _vote: Boolean;

		public function CreateOrVoteFeatureCommand(name: String, feature: int=-1, vote: Boolean=true) {
			super();
			_name = name;
			_feature = feature;
			_vote = vote;
		}

		override protected function makeOperation(): Object {
			var op: Object = {
					name: Cst.OP_CREATE_FEATURE,
					vote: _vote,
					userid: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					value: _name  
				};
			if (_feature > 0) {
				op.featureId = _feature;
			}
			return op;
		}
	}
}