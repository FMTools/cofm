package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class CreateOrVoteDescriptionCommand extends CommitOperationCommand {
		private var _feature: int;
		private var _des: String;
		private var _vote: Boolean;

		public function CreateOrVoteDescriptionCommand(feature: int, des: String, vote: Boolean=true) {
			super();
			_feature = feature;
			_des = des;
			_vote = vote;
		}

		override protected function makeOperation(): Object {
			return {
					name: Cst.OP_ADD_DES,
					vote: _vote,
					userid: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					featureId: _feature,
					value: _des  
				};
		}
	}
}