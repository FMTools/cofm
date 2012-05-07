package cofm.command.composite
{
	import cofm.event.*;
	import cofm.model.*;
	import cofm.util.*;
	
	import flash.events.Event;
	import cofm.command.AddAttributeCommand;
	import cofm.command.AddEnumAttributeCommand;
	import cofm.command.AddNumericAttributeCommand;
	import cofm.command.CreateModelCommand;
	import cofm.command.EditAddBinRelationTypeCommand;
	import cofm.command.EditAddEntityTypeCommand;
	import cofm.command.ICommand;
	import cofm.command.IDurableCommand;
	
	public class CreateModelCompositeCommand implements IDurableCommand
	{
		private var _info: Object;
		private var _commandQueue: Array;
		private var _modelId: int;
		
		private var _readyEntityTypeCount: int;
		
		private var _activated: Boolean;
		private var _bintypesArranged: Boolean;
		
		private var _executeIndex: int;
		
		public function CreateModelCompositeCommand(info: Object)
		{
			_info = info;
			
			ClientEvtDispatcher.instance().addEventListener(
				ModelCreateEvent.SUCCESS, onModelCreated);
			ClientEvtDispatcher.instance().addEventListener(
				OperationCommitEvent.COMMIT_SUCCESS, onOperationCommitted);
			
			_executeIndex = 0;
			_readyEntityTypeCount = 0;
			_modelId = -1;
			_activated = true;
			_bintypesArranged = false;
			_commandQueue = new Array();
		}
		
	    public function execute(): void {
			// Start with a CreateModelCommand
			new CreateModelCommand(_info.name, _info.description).execute();
		}
		
		private function finishExecution(): void {
			this._activated = false;
			ClientEvtDispatcher.instance().removeEventListener(
				ModelCreateEvent.SUCCESS, onModelCreated);
			ClientEvtDispatcher.instance().removeEventListener(
				OperationCommitEvent.COMMIT_SUCCESS, onOperationCommitted);
			
			ClientEvtDispatcher.instance().dispatchEvent(
				new ModelSelectEvent(ModelSelectEvent.SELECTED, this._modelId, _info.name));
		}
		
		private function onModelCreated(evt: ModelCreateEvent): void {
			if (!this._activated) {
				return;
			}
			this._modelId = int(evt.model.@id);
			
			// Followed by several EditAddEntityTypeCommands
			for (var i: int = 0; i < (_info.entypes as Array).length; i++) {
				var entype: Object = _info.entypes[i];					
				_commandQueue.push(new EditAddEntityTypeCommand(
					entype.typeName, entype.superId, this._modelId, -1,
					i // The command handle is its index in entypes
				)); 
			}
			
			// Execute the first command in the queue.
			if (_commandQueue[_executeIndex] != null) {
				ICommand(_commandQueue[_executeIndex]).execute();
				_executeIndex++;
				
			} else {
				// No entity types or binary relation types are specified, so end here.  
				finishExecution();
			}
		}
		
		private function onOperationCommitted(evt: OperationCommitEvent): void {
			if (!this._activated ||
				this._modelId < 0) {  // This function must be executed AFTER the model has been created.
				return;
			}
			var data: Object = evt.response;
			
			// If the EditAddEntityType request has committed from this composite command
			// (i.e. Command_Handle > 0)
			if (Cst.REQ_EA_ENTITY_TYPE == data[Cst.FIELD_RSP_SOURCE_NAME] &&
				int(data[Cst.FIELD_CMD_HANDLE]) >= 0) {
				
				_readyEntityTypeCount++;
				var index: int = int(data[Cst.FIELD_CMD_HANDLE]);
				
				// set the typeId for the entity type
				_info.entypes[index].typeId = int(data["typeId"]);
				
				// change the superId for its sub-type commands
				for each (var cmd: Object in _commandQueue) {
					if (cmd is EditAddEntityTypeCommand) {
						var c: EditAddEntityTypeCommand = EditAddEntityTypeCommand(cmd);
						if (c._superId == _info.entypes[index].id) {
							c._superId = int(data["typeId"]);
						}
					}
				}
				
				// Followed by several EditAddAttributeDefCommands for the entity type
				for each (var attrDef: Object in _info.entypes[index].attrDefs) {
					if (Cst.ATTR_TYPE_ENUM == String(attrDef.type)) {
						_commandQueue.push(new AddEnumAttributeCommand(
							attrDef.name, attrDef.enums, 
							int(data["typeId"]), 
							ModelUtil.isTrue(attrDef.multi), 
							ModelUtil.isTrue(attrDef.dup), 
							this._modelId));
					} else if (Cst.ATTR_TYPE_NUMBER == String(attrDef.type)) {
						_commandQueue.push(new AddNumericAttributeCommand(
							attrDef.name, 
							attrDef.min, attrDef.max, attrDef.unit,
							int(data["typeId"]), 
							attrDef.multi, attrDef.dup, 
							this._modelId));
					} else {
						_commandQueue.push(new AddAttributeCommand(
							attrDef.name, attrDef.type, 
							int(data["typeId"]), 
							attrDef.multi, attrDef.dup, 
							this._modelId));
					}
				}
			}

			
			// If all entity types are created, we can create the binary relations now.
			if (!_bintypesArranged && 
				_readyEntityTypeCount >= (_info.entypes as Array).length) {
				for each (var bintype: Object in _info.bintypes) {
					var srcId: int = getActualEntityId(String(bintype.sourceId));
					var targetId: int = getActualEntityId(String(bintype.targetId));
					if (srcId > 0 && targetId > 0) {
						_commandQueue.push(new EditAddBinRelationTypeCommand(
							bintype.typeName, srcId, targetId,
							ModelUtil.isTrue(bintype.hier), 
							ModelUtil.isTrue(bintype.dir),
							this._modelId, -1));
						trace("________BinRel CMD ______ ready entity type = " + _readyEntityTypeCount);
						trace(" = " + bintype.typeName);
					}
				}
				_bintypesArranged = true;
			}
			
			// Execute the next command.
			if (_commandQueue[_executeIndex] != null) {
				ICommand(_commandQueue[_executeIndex]).execute();
				_executeIndex++;
			} else {
				finishExecution();
			}
		}
		
		private function getActualEntityId(id: String): int {
			for each (var entype: Object in _info.entypes) {
				if (String(entype.id) == id) {
					return int(entype.typeId);
				}
			}	
			return -1;
		}
		
		public function redo(): void {
		}
		
		public function undo(): void {
		}
		
		public function setDurable(val:Boolean): void {
		}
		
		public function handleResponse(data:Object): void {
			
		}
		public function getId(): int {
			return -1;
		}
		
	}
}