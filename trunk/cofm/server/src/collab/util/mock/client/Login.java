package collab.util.mock.client;

import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.*;

public class Login implements Handler {
	
	static Logger logger = Logger.getLogger(Login.class);
	
	private MockClient client;
	
	public Login(MockClient client) {
		this.client = client;
		client.addHandler(Resources.REQ_LOGIN, this);
	}

	@Override
	public void recv(Response.Body body) {
		Response.Body.Source src = body.getSource();
		if (Resources.RSP_SUCCESS.equals(body.getStatus())) {
			client.onSuccess(body);
		} else if (Resources.RSP_DENIED.equals(body.getStatus())) {
			client.onDenied(body);
		} else {
			client.onError(body);
		}
	}

	@Override
	public Request send(HandlerOptions options) {
		User user = client.randomUser();
		Request r = new Request();
		r.setName(Resources.REQ_LOGIN);
		r.setUser(user.getName());
		return r;
	}

}
