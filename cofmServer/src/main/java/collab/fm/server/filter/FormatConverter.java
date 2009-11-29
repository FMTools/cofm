package collab.fm.server.filter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.*;

import collab.fm.server.bean.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.util.Resources;
import collab.fm.server.util.BeanUtil;

public class FormatConverter extends Filter {

	static Logger logger = Logger.getLogger(FormatConverter.class);
	
	public FormatConverter(String filterName) {
		super(filterName);
	}

	@Override
	protected Request doFilterRequest(Request request) {
		try {
			// json to Request, Request.data needs further identification
			String json = (String)request.getData();
			Request result = BeanUtil.jsonToBean(json, Request.class, null);
			result.setAddress(request.getAddress());
			return result;
		} catch (Exception e){
			///~locale
			onFilterError(request, Resources.REQ_ERROR_FORMAT,
					MessageFormat.format(Resources.MSG_ERROR_EXCEPTION, e.getMessage()), e);
			return null;
		} 
	}

	@Override
	protected Response doFilterResponse(Response response) {
		try {
			// Response to JSON string
			String json = BeanUtil.beanToJson(response.getBody());
			response.setBody(json);
			return response;
		} catch (Exception e) {
			///~locale
			onFilterError(response, Resources.RSP_ERROR_FORMAT,
					MessageFormat.format(Resources.MSG_ERROR_EXCEPTION, e.getMessage()), e);
			return null;
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	
}
