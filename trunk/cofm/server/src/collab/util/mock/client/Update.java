package collab.util.mock.client;

import java.util.*;

import net.sf.ezmorph.*;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.json.util.JSONUtils;

import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.*;
import collab.util.Utils;

public class Update implements Handler {
	
	static Logger logger = Logger.getLogger(Update.class);
	private MockClient client;
	
	public Update(MockClient client) {
		this.client = client;
		client.addHandler(Resources.REQ_UPDATE, this);
	}
	
	@Override
	public void recv(Response.Body body) {
		Response.Body.Source src = body.getSource();
		if (Resources.RSP_SUCCESS.equals(body.getStatus())) {
			client.onSuccess(body);
			try {
				client.removeAllFeatures();

				// body.getData() == list of Features
				Map<String, Class> map = new HashMap<String, Class>();
				map.put("existence", Votable.class);
				map.put("mandatory", Votable.class);
				map.put("names", Votable.class);
				map.put("descriptions", Votable.class);
				map.put("require", Votable.class);
				map.put("exclude", Votable.class);
				map.put("children", Votable.class);
				List features = (List) Utils.jsonToBean(body.getData(),
						List.class, Feature.class, map);

				for (Object obj : features) {
					// Cast object to feature
					MorpherRegistry reg = JSONUtils.getMorpherRegistry();
					reg.registerMorpher(new BeanMorpher(Feature.class, reg));

					Feature feature = (Feature) reg.morph(Feature.class, obj);

					// Cast List to List<Votable> for attributes of feature
					feature.setChildren(Utils.castBeanList(feature
							.getChildren(), LinkedList.class, Votable.class));
					feature.setDescriptions(Utils
							.castBeanList(feature.getDescriptions(),
									LinkedList.class, Votable.class));
					feature.setExclude(Utils.castBeanList(feature.getExclude(),
							LinkedList.class, Votable.class));
					feature.setRequire(Utils.castBeanList(feature.getRequire(),
							LinkedList.class, Votable.class));
					feature.setNames(Utils.castBeanList(feature.getNames(),
							LinkedList.class, Votable.class));

					client.addFeature(feature);

				}
				client.printFeatures();
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
		r.setName(Resources.REQ_UPDATE);
		r.setUser(client.randomUser().getName());
		r.setData(new Integer(0)); // get all features
		return r;
	}

}
