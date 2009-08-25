package collab.util.mock.client;

import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.*;

public class Login implements MockRequest {
	
	static Logger logger = Logger.getLogger(Login.class);
	
	private MockClient client;
	
	public Login(MockClient client) {
		this.client = client;
	}

	@Override
	public void recv(Response rsp) {
		Response.Body body = (Response.Body)rsp.getBody();
		Response.Body.Source src = body.getSource();
		if (Resources.REQ_LOGIN.equals(src.getName())) {
			if (Resources.RSP_SUCCESS.equals(body.getStatus())) {
				client.onSuccess(body);
			} else if (Resources.RSP_DENIED.equals(body.getStatus())) {
				client.onDenied(body);
			} else {
				client.onError(body);
			}
			return;
		}
		client.onBadMethod(body, Resources.REQ_LOGIN);
	}

	@Override
	public Request send(Object options) {
		User user = client.randomUser();
		Request r = new Request();
		r.setName(Resources.REQ_LOGIN);
		r.setUser(user.getName());
		return r;
	}

}
