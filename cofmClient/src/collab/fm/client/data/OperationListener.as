package collab.fm.client.data {
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;

	public class OperationListener {
		public function OperationListener() {
			ClientEvtDispatcher.instance().addEventListener(
				OperationCommitEvent.SUCCUESS, onOperationCommit);
		}

		// For details about operations, see server.bean.operation package.
		protected function onOperationCommit(evt: OperationCommitEvent): void {
			for each (var op: Object in evt.operations) {
				var opName: String = op["name"];
				switch (opName) {
					case Cst.OP_ADD_DES:
						handleAddDescription(op);
						break;
					case Cst.OP_ADD_NAME:
						handleAddName(op);
						break;
					case Cst.OP_CREATE_FEATURE:
						handleCreateFeature(op);
						break;
					case Cst.OP_CREATE_RELATIONSHIP:
						handleCreateRelationship(op);
						break;
					case Cst.OP_SET_OPT:
						handleSetOpt(op);
						break;
				}
			}
		}

		/*abstract*/
		protected function handleAddDescription(op: Object): void {

		}

		/*abstract*/
		protected function handleAddName(op: Object): void {

		}

		/*abstract*/
		protected function handleCreateFeature(op: Object): void {

		}

		/*abstract*/
		protected function handleCreateRelationship(op: Object): void {

		}

		/*abstract*/
		protected function handleSetOpt(op: Object): void {

		}

	}
}