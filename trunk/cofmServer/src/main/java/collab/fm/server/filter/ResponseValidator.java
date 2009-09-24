package collab.fm.server.filter;

import org.apache.log4j.Logger;

import collab.fm.server.bean.Request;
import collab.fm.server.bean.Response;

public class ResponseValidator extends Filter {

	static Logger logger = Logger.getLogger(ResponseValidator.class);
	
	public ResponseValidator(String filterName) {
		super(filterName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Request doFilterRequest(Request request) {
		return request;
	}

	@Override
	protected Response doFilterResponse(Response response) {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
