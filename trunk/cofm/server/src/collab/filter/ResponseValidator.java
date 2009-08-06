package collab.filter;

import collab.data.Request;
import collab.data.Response;

public class ResponseValidator extends Filter {

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
		return null;
	}

}
