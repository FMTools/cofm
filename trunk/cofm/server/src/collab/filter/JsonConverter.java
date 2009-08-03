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
			return request;
		} catch (Exception e){
			///~locale
			request.filterError(Resources.get(Resources.REQ_ERROR_FORMAT));
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
			response.filterError(Resources.get(Resources.RSP_ERROR_FORMAT));
			response.filterMessage("Exception: " + e.getMessage());
			return null;
		}
	}
	
///~
}
