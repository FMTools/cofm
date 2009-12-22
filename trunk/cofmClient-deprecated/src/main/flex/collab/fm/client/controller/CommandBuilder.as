package collab.fm.client.controller {
	import collab.fm.client.command.*;
	import collab.fm.client.util.Resources;
	
	import flash.utils.Dictionary;
	import flash.utils.getDefinitionByName;
	import flash.utils.getQualifiedClassName;


	public class CommandBuilder {

		private final static var forwardedPrefix: String = "forwarded";
		
		private final static var rspNames: Array = {
			Resources.RSP_ERROR,
			Resources.RSP_STALE,
			Resources.RSP_SUCCESS
		};

		private var commandMap: Dictionary = new Dictionary();

		private static var instance: CommandBuilder = new CommandBuilder();

		public static function getInstance(): CommandBuilder {
			return instance;
		}

		public function buildCommand(info: Object): Command {
			var name: String = info[Resources.FIELD_CMD_MAIN_NAME] as String;
			//1. If it is a response of previous command, just return it.
			if (isResponse(name)) {
				return CommandBuffer.getInstance().getCommandAt(info[Resources.FIELD_CMD_ID]);
			}
			//2. if name=="forward" then get "requestName" from info (See Response.java for details)
			if (Resources.RSP_FORWARD == name) {
				name = forwardedPrefix + name;
			} 
			
			//3. return new command(info);
			var commandClassName: String = commandMap[name] as String;
			var commandClass: Class = getDefinitionByName(commandClassName) as Class;
			return new commandClass(info) as Command;
		}

		private function CommandBuilder() {
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
			
			commandMap[Resources.OP_ADD_DES] = getQualifiedClassName(AddDescriptionCommand);
			commandMap[Resources.OP_ADD_NAME] = getQualifiedClassName(AddNameCommand);
			commandMap[Resources.OP_CREATE_FEATURE] = getQualifiedClassName(CreateFeatureCommand);
			commandMap[Resources.OP_CREATE_RELATIONSHIP] = getQualifiedClassName(CreateRelationshipCommand);
			commandMap[Resources.OP_SET_OPT] = getQualifiedClassName(SetOptionalityCommand);
			commandMap[Resources.CMD_UI_CREATE_FEATURE] = getQualifiedClassName(UICreateFeatureCommand);
			commandMap[Resources.REQ_LIST_USER] = getQualifiedClassName(ListUserCommand);
			commandMap[Resources.REQ_LOGIN] = getQualifiedClassName(LoginCommand);
			commandMap[Resources.REQ_LOGOUT] = getQualifiedClassName(LogoutCommand);
			commandMap[Resources.REQ_REGISTER] = getQualifiedClassName(RegisterCommand);
			commandMap[Resources.REQ_UPDATE] = getQualifiedClassName(UpdateCommand);
			commandMap[forward(Resources.REQ_COMMIT)] = getQualifiedClassName(ForwardedCommitCommand);
		}

	}
}