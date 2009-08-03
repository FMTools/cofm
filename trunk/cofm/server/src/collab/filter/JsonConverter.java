package collab.filter;

import org.apache.commons.beanutils.*;
import net.sf.json.*;

import collab.data.*;

public class JsonConverter extends Filter {
//~sketch
	public JsonConverter(String name, Filter prev, Filter next) {
		super(name, prev, next);
	}

	@Override
	protected Request doFilterRequest(Request request) {
		try {
			String json = request.body().toString();
			JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(json);
			DynaBean bean = (DynaBean)JSONSerializer.toJava(jsonObject);
			request.body(bean);
			request.name((String)bean.get(Resources.get(Resources.PRTCL_NAME_FIELD)));
			return request;
		} catch (Exception e){
			///~locale
			request.name(Resources.get(Resources.REQ_BAD));
			request.filterMessage("Exception: " + e.getMessage());
			return null;
		}
	}

	@Override
	protected Response doFilterResponse(Response response) {
		try {
			JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(response.body());
			response.body(jsonObject.toString());
			return response;
		} catch (Exception e) {
			///~locale
			response.filterMessage("Exception: " + e.getMessage());
			return null;
		}
	}
	
///~
}
