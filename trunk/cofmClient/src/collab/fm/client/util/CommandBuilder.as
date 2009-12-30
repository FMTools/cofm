package collab.fm.client.controller {
	import collab.fm.client.command.*;
	import collab.fm.client.util.Cst;
	
	import flash.utils.Dictionary;
	import flash.utils.getDefinitionByName;
	import flash.utils.getQualifiedClassName;


	public class CommandBuilder {

		private final static var forwardedPrefix: String = "forwarded";
		
		private final static var rspNames: Array = {
			Cst.RSP_ERROR,
			Cst.RSP_STALE,
			Cst.RSP_SUCCESS
		};

		private var commandMap: Dictionary = new Dictionary();

		private static var instance: CommandBuilder = new CommandBuilder();

		public static function getInstance(): CommandBuilder {
			return instance;
		}

		public function buildCommand(info: Object): Command {
			var name: String = info[Cst.FIELD_RSP_NAME] as String;
			//1. If it is a response of previous command, just return it.
			if (isResponse(name)) {
				var cmd:DurableCommand = CommandBuffer.getInstance().getCommand(info[Cst.FIELD_CMD_ID]);
				cmd.setResponse(info);
				return cmd;
			}
			//2. if name=="forward" then get "requestName" from info (See Response.java for details)
			if (Cst.RSP_FORWARD == name) {
				name = forwardedPrefix + name;
			} 
			
			//3. return new command(info);
			var commandClassName: String = commandMap[name] as String;
			var commandClass: Class = getDefinitionByName(commandClassName) as Class;
			return new commandClass(info) as Command;
		}

		public function CommandBuilder() {
			initCommandMap();
		}
		
		private function isResponse(name: String): Boolean {
			return rspNames.indexOf(name) >= 0;
		}
		
		private function initCommandMap(): void {
			/*
			 Command Hierarchy: 
			   Durable  { public void setDurable(); }
			     CreateFeatureCommand
			     AddNameCommand
			     AddDescriptionCommand
			     SetOptionalityCommand
			     UICreateFeatureCommand
			        { execute: check name has not been used; new CreateFeatureCommand().execute(); }
			     LoginCommand
			     RegisterCommand
			     LogoutCommand
			     UpdateCommand
			     ListUserCommand
			     ...   
			   Forwarded
			     ForwardedCommitCommand
			        { execute: op = get op from info; cmd = CommandBuilder.build(op); cmd.setDurable(false); cmd. execute(); }
			     ForwardedModelCheckingCommand
			     ...
			*/
			
			function forward(name: String): Stirng {
				return forwardedPrefix + name;
			}
			
			commandMap[Cst.OP_ADD_DES] = getQualifiedClassName(AddDescriptionCommand);
			commandMap[Cst.OP_ADD_NAME] = getQualifiedClassName(AddNameCommand);
			commandMap[Cst.OP_CREATE_FEATURE] = getQualifiedClassName(CreateFeatureCommand);
			commandMap[Cst.OP_CREATE_RELATIONSHIP] = getQualifiedClassName(CreateRelationshipCommand);
			commandMap[Cst.OP_SET_OPT] = getQualifiedClassName(SetOptionalityCommand);
			commandMap[Cst.CMD_UI_CREATE_FEATURE] = getQualifiedClassName(UICreateFeatureCommand);
			commandMap[Cst.REQ_LIST_USER] = getQualifiedClassName(ListUserCommand);
			commandMap[Cst.REQ_LOGIN] = getQualifiedClassName(LoginCommand);
			commandMap[Cst.REQ_LOGOUT] = getQualifiedClassName(LogoutCommand);
			commandMap[Cst.REQ_REGISTER] = getQualifiedClassName(RegisterCommand);
			commandMap[Cst.REQ_UPDATE] = getQualifiedClassName(UpdateCommand);
			commandMap[forward(Cst.REQ_COMMIT)] = getQualifiedClassName(ForwardedCommitCommand);
		}
	}
}