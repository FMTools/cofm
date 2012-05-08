package quiz.server.request;

import org.apache.log4j.Logger;

import quiz.server.bean.User;
import quiz.server.dao.DaoUtil;

public class LoadRequest implements Request {

	static Logger logger = Logger.getLogger(LoadRequest.class);
	private String name;
	
	public LoadRequest(String name) {
		this.name = name;
	}
	
	public Response handle() {
		User u = DaoUtil.getUserDao().getByName(name);
		if (u == null) {
			return new Response(Response.STATUS_BAD);
		}
		logger.info("LOAD: " + name + " - " + u.getAnsweredQuiz());
		return new Response(Response.STATUS_OK, u.getAnsweredQuiz());
	}

}
