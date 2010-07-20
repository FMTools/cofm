package collab.fm.client.command {
	import collab.fm.client.cmn.*;
	import collab.fm.client.data.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class CreateOrVoteBinaryRelationshipCommand extends CommitOperationCommand {
		private var _type: String;
		private var _left: int;
		private var _right: int;
		private var _relationId: int;
		private var _vote: Boolean;

		public function CreateOrVoteBinaryRelationshipCommand(type: String, left: int, right: int, relationId: int=-1, vote: Boolean=true) {
			super();
			_type = type;
			_left = left;
			_right = right;
			_relationId = relationId;
			_vote = vote;
		}

		override protected function makeOperation(): Object {
			var op: Object = {
					name: Cst.OP_CREATE_RELATIONSHIP,
					vote: _vote,
					userid: UserList.instance.myId,
					modelId: ModelCollection.instance.currentModelId,
					type: _type,
					leftFeatureId: _left,
					rightFeatureId: _right				
				};
			if (_relationId > 0) {
				op.relationshipId = _relationId;
			}
			return op;
		}

	}
}