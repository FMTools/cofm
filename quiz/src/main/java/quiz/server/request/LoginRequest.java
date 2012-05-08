package quiz.server.request;

import org.apache.log4j.Logger;

import quiz.server.dao.DaoUtil;

public class LoginRequest implements Request {

	static Logger logger = Logger.getLogger(LoginRequest.class);
	private String name;
	private String vcode;
	
	public LoginRequest(String name, String vcode) {
		this.name = name;
		this.vcode = vcode;
	}
	
	public Response handle() {
		if (DaoUtil.getUserDao().getByNameAndVCode(name, vcode) == null) {
			logger.info("LOGIN: " + name);
			return new Response(Response.STATUS_BAD);
		}
		return new Response(Response.STATUS_OK);
	}
	
}
