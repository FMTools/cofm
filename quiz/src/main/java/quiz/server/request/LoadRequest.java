package quiz.server.request;

import quiz.server.bean.User;
import quiz.server.dao.DaoUtil;

public class LoadRequest implements Request {

	private String name;
	
	public LoadRequest(String name) {
		this.name = name;
	}
	
	public Response handle() {
		User u = DaoUtil.getUserDao().getByName(name);
		if (u == null) {
			return new Response(Response.STATUS_BAD);
		}
		return new Response(Response.STATUS_OK, u.getAnsweredQuiz());
	}

}
