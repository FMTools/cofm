package cofm.model
{
	public interface IOperationListener {
		function handleVoteAddEntity(op: Object): void;
		function handleVoteAddBinRel(op: Object): void;
		
		function handleEditAddAttributeDef(op: Object): void;
		function handleEditAddEnumAttributeDef(op: Object): void;
		function handleEditAddNumericAttributeDef(op: Object): void;
		
		function handleEditAddEntityType(op: Object): void;
		function handleEditAddBinRelType(op: Object): void;
		
		function handleVoteAddValue(op: Object): void;
		
		function handleInferVoteOnEntity(op: Object): void;
		function handleInferVoteOnRelation(op: Object): void;
	}
}