package collab.fm.server.filter;

import org.apache.log4j.Logger;

import collab.fm.server.bean.Request;
import collab.fm.server.bean.Response;

/**
 * Generate error values for request and response deliberately.
 * This filter is often placed between ResponseValidator and RequestValidator,
 * which means Actions will receive requests with correct format but wrong values, 
 * and/or clients will receive responses like that.
 * Useful for testing.
 * 
 * @author Yi Li
 *
 */
public class ErrorGenerator extends Filter {
	
	static Logger logger = Logger.getLogger(ErrorGenerator.class);

	public ErrorGenerator(String filterName) {
		super(filterName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Request doFilterRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response doFilterResponse(Response response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
