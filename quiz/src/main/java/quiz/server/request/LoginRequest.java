package quiz.server.request;

import quiz.server.dao.DaoUtil;

public class LoginRequest implements Request {

	private String name;
	private String vcode;
	
	public LoginRequest(String name, String vcode) {
		this.name = name;
		this.vcode = vcode;
	}
	
	public Response handle() {
		if (DaoUtil.getUserDao().getByNameAndVCode(name, vcode) == null) {
			return new Response(Response.STATUS_BAD);
		}
		return new Response(Response.STATUS_OK);
	}
	
}
