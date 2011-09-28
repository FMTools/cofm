package cofm.command.composite
{
	import cofm.command.IDurableCommand;
	import cofm.command.VoteAddBinRelationCommand;
	import cofm.command.VoteAddFeatureCommand;
	import cofm.command.VoteAddValueCommand;
	import cofm.event.ClientEvent;
	import cofm.event.ModelMinorChangeEvent;
	import cofm.event.OperationCommitEvent;
	import cofm.model.Model;
	import cofm.util.ClientEvtDispatcher;
	import cofm.util.Cst;
	
	import flash.events.Event;
	
	import mx.utils.StringUtil;
	
	public class CreateEntityCompositeCommand implements IDurableCommand
	{
		private var _typeId: int;
		private var _name: String;
		private var _des: String;
		private var _parentId: int;
		private var _entityId: int;
		private var _lastAttrName: String;
		private var _lastCommand: IDurableCommand;
		
		public function CreateEntityCompositeCommand(
			typeId: int, name: String, des: String, parentId: int = -1)
		{
			_typeId = typeId;
			_name = name;
			_des = des;
			_parentId = parentId;
			
			ClientEvtDispatcher.instance().addEventListener(ModelMinorChangeEvent.FEATURE_CREATED_LOCALLY, onFeatureCreatedLocally);
			ClientEvtDispatcher.instance().addEventListener(OperationCommitEvent.EXECUTED_ON_LOCAL, onOperationExecuted);
		}
		
		public function execute():void
		{
			this._lastCommand = new VoteAddFeatureCommand(this._typeId);
			this._lastCommand.execute();
		}
		
		private function onFeatureCreatedLocally(evt: ModelMinorChangeEvent): void {
			if (this._lastCommand.getId() == evt.sourceCommand) {
				this._entityId = int(evt.data);
				
				// Remove the listener
				ClientEvtDispatcher.instance().removeEventListener(
					ModelMinorChangeEvent.FEATURE_CREATED_LOCALLY, onFeatureCreatedLocally);
				
				// Create Name
				var newEntities: XMLList = Model.instance().entities.source.(@id==String(evt.data));
				if (newEntities.length() <= 0) {
					return;
				}
				var newEntity: XML = newEntities[0];
				this._lastAttrName = Cst.ATTR_FEATURE_NAME;
				this._lastCommand = new VoteAddValueCommand(
					Model.instance().getAttrIdByName(newEntity, Cst.ATTR_FEATURE_NAME),
					this._name, this._entityId);
				this._lastCommand.execute();
			}
		}
		
		private function onOperationExecuted(evt: OperationCommitEvent): void {
			if (this._lastCommand.getId() == int(evt.response[Cst.FIELD_RSP_SOURCE_ID])) {
				
				if (this._lastAttrName == Cst.ATTR_FEATURE_NAME) {
					this._lastAttrName = Cst.ATTR_FEATURE_DES;
					// Create Description
					if (mx.utils.StringUtil.trim(this._des) != "") {
						var entity: XML = Model.instance().getEntityById(String(this._entityId));
						if (entity != null) {
							this._lastCommand = new VoteAddValueCommand(
								Model.instance().getAttrIdByName(entity, Cst.ATTR_FEATURE_DES),
								this._des, this._entityId);
							this._lastCommand.execute();
						}
						
					} else {
						// Skip to Create Refinement
						createRefinement();
					}
				} else if (this._lastAttrName == Cst.ATTR_FEATURE_DES) {
					createRefinement();
				}
			}
		}
		
		private function createRefinement(): void {
			if (this._parentId > 0) {
				var parent: XML = Model.instance().getEntityById(String(this._parentId));
				var me: XML = Model.instance().getEntityById(String(this._entityId));
				if (parent == null || me == null) {
					return;
				}
				var binrels: XMLList = Model.instance().getBinRelationTypesByEnds(
					int(parent.@typeId), int(me.@typeId));
				for each (var r: Object in binrels) {
					if (Model.instance().isRefinement(XML(r))) {  // if is a refinement
						new VoteAddBinRelationCommand(
							int(r.@id), this._parentId, this._entityId).execute();
						break;
					}
				}
			}
			
			// Remove event listeners
			ClientEvtDispatcher.instance().removeEventListener(OperationCommitEvent.EXECUTED_ON_LOCAL, onOperationExecuted);
		}
		
		public function redo():void
		{
		}
		
		public function undo():void
		{
		}
		
		public function setDurable(val:Boolean):void
		{
		}
		
		public function handleResponse(data:Object):void
		{
		}
		
		public function getId(): int {
			return -1;
		}
		
		
	}
}