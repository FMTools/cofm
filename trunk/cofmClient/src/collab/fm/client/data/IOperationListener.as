package collab.fm.client.data {

	public interface IOperationListener {
		function handleAddDescription(op: Object): void;
		function handleAddName(op: Object): void;
		function handleCreateFeature(op: Object): void;
		function handleCreateBinaryRelationship(op: Object): void;
		function handleSetOpt(op: Object): void;

		function handleFeatureVotePropagation(op: Object): void;
		function handleRelationshipVotePropagation(op: Object): void;
	}
}