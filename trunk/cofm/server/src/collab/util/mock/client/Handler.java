package collab.util.mock.client;

import collab.data.Request;
import collab.data.Response.Body;

public interface Handler {
	public Request send(HandlerOptions options);
	public void recv(Body body);
}
