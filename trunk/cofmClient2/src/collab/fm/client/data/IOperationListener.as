package collab.fm.client.data {

	public interface IOperationListener {
		function handleVoteAddFeature(op: Object): void;
		function handleVoteAddBinRel(op: Object): void;
		
		function handleAddAttribute(op: Object): void;
		function handleAddEnumAttribute(op: Object): void;
		function handleAddNumericAttribute(op: Object): void;
		
		function handleVoteAddValue(op: Object): void;
		
		function handleInferVoteOnFeature(op: Object): void;
		function handleInferVoteOnRelation(op: Object): void;
	}
}