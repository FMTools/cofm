package collab.fm.client.cmn {

	import collab.fm.client.command.*;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	import collab.fm.client.data.*;

	// Dispatch server response to approriate commands which implements IForwardedCommand interface.
	public class ServerDataDispatcher {

		private static var rsps: Array = [
			Cst.RSP_ERROR,
			Cst.RSP_STALE,
			Cst.RSP_SUCCESS
			];

		private static function isResponse(name: String): Boolean {
			return rsps.indexOf(name) >= 0;
		}

		public static function dispatchData(data: Object): void {
			// get the resposne name
			var name: String = data[Cst.FIELD_RSP_NAME] as String;
			if (isResponse(name) && data[Cst.FIELD_RSP_SOURCE_ID] != null) {
				Msg.showResponse(name, data[Cst.FIELD_RSP_MESSAGE]);
				CommandBuffer.instance.getCommand(
					int(data[Cst.FIELD_RSP_SOURCE_ID])).handleResponse(data);
			} else if (Cst.RSP_FORWARD == name) {
				// do forwarded command
				name = data[Cst.FIELD_RSP_SOURCE_NAME] as String;
				switch (name) {
					case Cst.REQ_COMMIT:
						ClientEvtDispatcher.instance().dispatchEvent(
							new OperationCommitEvent(
							OperationCommitEvent.FORWARDED, 
							data["operations"] as Array));
						break;
					case Cst.REQ_EDIT:
						if (ModelCollection.instance.currentModelId == int(data["modelId"])) {
						ClientEvtDispatcher.instance().dispatchEvent(
							new FeatureSelectEvent(
								FeatureSelectEvent.OTHER_PEOPLE_SELECT_ON_TREE,
								int(data["featureId"]),
								null,  // feature name is omitted
								int(data["modelId"]),
								int(data["requesterId"])));
						}
						break;
				}
			} else if (Cst.RSP_SERVER_ERROR == name) {
				//TODO: report internal error
			}
		}
	}
}