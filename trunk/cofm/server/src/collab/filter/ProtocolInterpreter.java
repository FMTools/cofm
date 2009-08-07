package collab.filter;

import java.text.MessageFormat;

import org.apache.commons.beanutils.*;
import net.sf.json.*;
import net.sf.json.util.PropertyFilter;

import collab.data.*;

public class ProtocolInterpreter extends Filter {

	public ProtocolInterpreter(String filterName) {
		super(filterName);
	}

	@Override
	protected Request doFilterRequest(Request request) {
		try {
			// json to DynaBean
			String json = (String)request.getData();
			JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(json);
			DynaBean bean = (DynaBean)JSONSerializer.toJava(jsonObject);
			
			// request ID and Name must exist.
			request.setId((Integer)bean.get(Resources.get(Resources.REQ_FIELD_ID)));
			request.setName((String)bean.get(Resources.get(Resources.REQ_FIELD_NAME)));
			
			// request User and Data are optional
			try {
				request.setUser((String)bean.get(Resources.get(Resources.REQ_FIELD_USER)));
			} catch (Exception e) {
				request.setUser(null);
			}
			try {
/*				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					public boolean apply(Object source, String name, Object value) {
						// Ignore all "top-level" fields of the json object, except "data"
						if (source instanceof JSONObject) {
							if (((JSONObject) source).has(Resources.get(Resources.REQ_FIELD_DATA))) {
								return true;
							}
						}
						return false;
					}
				});    
				DynaBean dataBean = (DynaBean)JSONSerializer.toJava(jsonObject, jsonConfig); 
				request.setData(dataBean.get("data"));*/
				request.setData(bean.get("data"));
			} catch (Exception e) {
				request.setData(null);
			}
			return request;
		} catch (Exception e){
			///~locale
			onFilterError(request, Resources.get(Resources.REQ_ERROR_FORMAT),
					MessageFormat.format(Resources.get(Resources.MSG_ERROR_EXCEPTION), e.getMessage()));
			return null;
		} 
	}

	@Override
	protected Response doFilterResponse(Response response) {
		try {
			JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(response.getBody());
			response.setBody(jsonObject.toString());
			return response;
		} catch (Exception e) {
			///~locale
			onFilterError(response, Resources.get(Resources.RSP_ERROR_FORMAT),
					MessageFormat.format(Resources.get(Resources.MSG_ERROR_EXCEPTION), e.getMessage()));
			return null;
		}
	}
	
}
