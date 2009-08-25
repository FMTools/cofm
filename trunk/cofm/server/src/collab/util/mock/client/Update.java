package collab.util.mock.client;

import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.Feature;

public class Update implements MockRequest {
	
	static Logger logger = Logger.getLogger(Update.class);
	private MockClient client;
	
	public Update(MockClient client) {
		this.client = client;
	}
	@Override
	public void recv(Response rsp) {
		Response.Body body = (Response.Body)rsp.getBody();
		Response.Body.Source src = body.getSource();
		if (Resources.REQ_UPDATE.equals(src.getName())) {
			if (Resources.RSP_SUCCESS.equals(body.getStatus())) {
				client.onSuccess(body);
				try {
					List features = (List)body.getData();
					client.removeAllFeatures();
					for (Object f: features) {
						client.addFeature((Feature)f);
					}
				} catch (Exception e) {
					logger.warn("Invalid response.", e);
				}
			} else if (Resources.RSP_DENIED.equals(body.getStatus())) {
				client.onDenied(body);
			} else {
				client.onError(body);
			}
			return;
		}
		client.onBadMethod(body, Resources.REQ_UPDATE);
	}

	@Override
	public Request send(Object options) {
		Request r = new Request();
		r.setName(Resources.REQ_UPDATE);
		r.setUser(client.randomUser().getName());
		r.setData(new Integer(0)); // get all features
		return r;
	}

}
