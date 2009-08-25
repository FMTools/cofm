package collab.util.mock.client;

import collab.data.Request;
import collab.data.Response;

public interface MockRequest {
	public Request send(Object options);
	public void recv(Response rsp);
}
