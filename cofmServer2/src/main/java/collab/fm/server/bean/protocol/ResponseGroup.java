package collab.fm.server.bean.protocol;

import java.util.List;

public class ResponseGroup {
	
	private static final int DEBUG_LENGTH = 100;
	
	private Response back;
	private Response peer;
	private Response broadcast;
	
	private List<String> targets;
	
	private String jsonBack;
	private String jsonPeer;
	private String jsonBroadcast;
	
	public String toString() {
		return "back: " + truncateForDebug(jsonBack) + "\n\t" +
				"broadcast: " + truncateForDebug(jsonBroadcast) + "\n\t" +
				"forward: " + truncateForDebug(jsonPeer);
	}
	
	private String truncateForDebug(String s) {
		if (s == null) {
			return "null";
		}
		else {
			if (s.length() > DEBUG_LENGTH) {
				return s.substring(0, DEBUG_LENGTH);
			}
				return s;
		}
	}
	
	public String getJsonBack() {
		return jsonBack;
	}

	public void setJsonBack(String jsonBack) {
		this.jsonBack = jsonBack;
	}

	public String getJsonPeer() {
		return jsonPeer;
	}

	public void setJsonPeer(String jsonPeer) {
		this.jsonPeer = jsonPeer;
	}

	public String getJsonBroadcast() {
		return jsonBroadcast;
	}

	public void setJsonBroadcast(String jsonBroadcast) {
		this.jsonBroadcast = jsonBroadcast;
	}

	public ResponseGroup() {
		
	}

	public Response getBack() {
		return back;
	}

	public void setBack(Response back) {
		this.back = back;
	}

	public Response getPeer() {
		return peer;
	}

	public void setPeer(Response peer) {
		this.peer = peer;
	}

	public Response getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(Response broadcast) {
		this.broadcast = broadcast;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}
	
}
