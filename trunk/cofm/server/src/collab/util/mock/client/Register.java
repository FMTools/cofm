package collab.util.mock.client;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.User;

public class Register implements Handler {

	static Logger logger = Logger.getLogger(Register.class);
	private MockClient client;
	
	public Register(MockClient client) {
		this.client = client;
		client.addHandler(Resources.REQ_REGISTER, this);
	}
	
	@Override
	public void recv(Response.Body body) {
		Response.Body.Source src = body.getSource();
			if (Resources.RSP_SUCCESS.equals(body.getStatus())) {
			client.onSuccess(body);
			try {
				DynaBean bean = (DynaBean) body.getData();
				User u = new User();
				u.setId((Integer) bean.get("id"));
				u.setName((String) bean.get("name"));
				client.addRegUser(u);
			} catch (Exception e) {
				logger.warn("Invalid response.", e);
			}
		} else if (Resources.RSP_DENIED.equals(body.getStatus())) {
			client.onDenied(body);
		} else {
			client.onError(body);
		}
	}

	@Override
	public Request send(HandlerOptions options) {
		Request r = new Request();
		r.setName(Resources.REQ_REGISTER);
		r.setUser(client.randomUserName());
		return r;
	}

}
