package collab.fm.server.bean.protocol;

import java.util.List;

public class ResponseGroup {
	
	private static final int DEBUG_LENGTH = 500;
	
	private Response back;
	private Response peer;
	private Response broadcast;
	
	private List<String> targets;
	
	private String jsonBack;
	private String jsonPeer;
	private String jsonBroadcast;
	
	public String toString() {
		return "back: " + truncateForDebug(back) + "\n\t" +
				"broadcast: " + truncateForDebug(broadcast) + "\n\t" +
				"forward: " + truncateForDebug(peer);
	}
	
	private String truncateForDebug(Response res) {
		if (res == null) {
			return "null";
		} else {
			return "Name=" + res.getName() + ", SrcClient=" + res.getRequestClientId() + ", Request=" + res.getRequestName() +
			", Message=" + res.getMessage();
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
