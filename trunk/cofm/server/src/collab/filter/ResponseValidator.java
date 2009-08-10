package collab.filter;

import org.apache.log4j.Logger;

import collab.data.Request;
import collab.data.Response;

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
