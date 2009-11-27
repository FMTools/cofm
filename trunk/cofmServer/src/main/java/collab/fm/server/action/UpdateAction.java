package collab.fm.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import collab.fm.server.bean.*;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.json.Response;
import collab.fm.server.persistence.*;
import collab.fm.server.util.Resources;
import collab.fm.server.controller.*;


public class UpdateAction extends Action {
	
	static Logger logger = Logger.getLogger(UpdateAction.class);
	
	public UpdateAction(Controller controller) {
		super(new String[]{Resources.REQ_UPDATE}, controller);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Response> process(Object input) {
		// TODO mock, return all feature.
		List<Response> result = new ArrayList<Response>();
		/*Response r = new Response();
		writeSource(r, (Request)input);
		
		Integer begin = (Integer)((Request)input).getData();
		List<Feature> features = dp.getRecentFeatures(begin);
		write(r, Response.TYPE_BACK, Resources.RSP_SUCCESS, features);
		result.add(r);*/
		return result;
	}

}
