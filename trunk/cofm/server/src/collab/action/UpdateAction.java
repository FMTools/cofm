package collab.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.*;
import collab.server.Controller;
import collab.storage.DataProvider;

public class UpdateAction extends Action {
	
	static Logger logger = Logger.getLogger(UpdateAction.class);
	
	public UpdateAction(Controller controller,
			DataProvider dp) {
		super(new String[]{Resources.REQ_UPDATE}, controller, dp);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public List<Response> process(Object input) {
		// TODO mock, return all feature.
		List<Response> result = new ArrayList<Response>();
		Response r = new Response();
		writeSource(r, (Request)input);
		
		Integer begin = (Integer)((Request)input).getData();
		List<Feature> features = dp.getRecentFeatures(begin);
		write(r, Response.TYPE_BACK, Resources.RSP_SUCCESS, features);
		result.add(r);
		return result;
	}

}
