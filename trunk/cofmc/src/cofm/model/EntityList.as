package cofm.model
{
	import cofm.command.*;
	import cofm.event.*;
	import cofm.util.*;
	
	import mx.collections.XMLListCollection;
	
	public class EntityList implements IOperationListener
	{
		[Bindable] public var entities: XMLListCollection;
		
		public var baseTypeId: String;
		
		public function EntityList()
		{
			entities = new XMLListCollection();
			baseTypeId = "-1";
			Model.instance().registerSubView(this);
		}
		
		public function filterByType(typeId: String): void {
			baseTypeId = typeId;
			var validTypes: XMLList = Model.instance().getEntityTypeHierarchy(typeId);
			var result: XML = <result/>;
			for each (var type: Object in validTypes) {
				var tId: String = String(type.@id);
				result.appendChild(Model.instance().getEntityByType(tId).copy());
			}
			entities.source = result.entity;
		}
		
		public function handleVoteAddEntity(op:Object):void
		{
			// Handle entity creation and removal in the baseType
			var type: String = String(op["typeId"]);
			if (!Model.instance().isSubType(type, this.baseTypeId)) {
				return;
			}
			
			var id: String = String(op["entityId"]);
			if (op[Model.IS_NEW_ELEMENT] == true) {
				var e: XML = Model.instance().getEntityById(id);
				if (e != null) {
					this.entities.addItem(e.copy());
				}
				return;
			}
			
			if (op[Model.SHOULD_DELETE_ELEMENT] == true) {
				delete this.entities.source.(@id==id)[0];
			}
		}
		
		public function handleVoteAddBinRel(op:Object):void
		{
			// Do nothing
		}
		
		public function handleEditAddAttributeDef(op:Object):void
		{
			// Do nothing
		}
		
		public function handleEditAddEnumAttributeDef(op:Object):void
		{
			// Do nothing
		}
		
		public function handleEditAddNumericAttributeDef(op:Object):void
		{
			// Do nothing
		}
		
		public function handleEditAddEntityType(op:Object):void
		{
			// Do nothing, because type ID cannot be changed.
		}
		
		public function handleEditAddBinRelType(op:Object):void
		{
			// Do nothing, because the source and target type of the binary relation cannot be changed.
		}
		
		public function handleVoteAddValue(op:Object):void
		{
			// Do nothing
		}
		
		public function handleInferVoteOnEntity(op:Object):void
		{
			// Do nothing, because inferred votes cannot delete an entity.
		}
		
		public function handleInferVoteOnRelation(op:Object):void
		{
			// Do nothing
		}
	}
}