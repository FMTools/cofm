package quiz.server;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import quiz.server.request.LoadRequest;
import quiz.server.request.LoginRequest;
import quiz.server.request.Response;
import quiz.server.request.SaveRequest;
import quiz.server.dao.HibernateUtil;

public class QuizServlet extends HttpServlet {

	static Logger logger = Logger.getLogger(QuizServlet.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1861041724700087840L;

	private static final String KEY_ACTION = "action";
	private static final String KEY_USER = "userName";
	private static final String KEY_PWD = "userPwd";
	private static final String KEY_QUIZ = "quizId";
	private static final String KEY_DECISION = "decision";
	private static final String KEY_TIME = "time";
	private static final String KEY_SCORE = "score";
	
	private static final String ACTION_HANDSHAKE = "handshake";
	private static final String ACTION_LOGIN = "login";
	private static final String ACTION_LOAD = "progress";
	private static final String ACTION_SAVE = "save";
	
	private Set<String> loginUsers = new TreeSet<String>();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		String action = req.getParameter(KEY_ACTION);
		
		res.setContentType("text/plain");
		
		if (action.equals(ACTION_HANDSHAKE)) {
			res.getWriter().write("OK");
		} else {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			if (action.equals(ACTION_LOGIN)) {
				handleLogin(req, res);
			} else if (action.equals(ACTION_LOAD)) {
				handleLoad(req, res);
			} else if (action.equals(ACTION_SAVE)) {
				handleSave(req, res);
			}
			
			session.getTransaction().commit();
		}
		
	}

	private void handleSave(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String user = req.getParameter(KEY_USER);
		int quizId = Integer.valueOf(req.getParameter(KEY_QUIZ));
		int answer = Integer.valueOf(req.getParameter(KEY_DECISION));
		long time = Long.valueOf(req.getParameter(KEY_TIME));
		
		if (loginUsers.contains(user)) {
			Response result = new SaveRequest(user, quizId, answer, time).handle();
			res.getWriter().write(result.getMessage());
		}
		res.getWriter().write("ERROR");
	}

	private void handleLoad(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String user = req.getParameter(KEY_USER);
		if (loginUsers.contains(user)) {
			Response result = new LoadRequest(user).handle();
			res.getWriter().write(result.getMessage());
		}
		res.getWriter().write("ERROR");
	}

	private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String user = req.getParameter(KEY_USER);
		String pwd = req.getParameter(KEY_PWD);
		Response result = new LoginRequest(user, pwd).handle();
		if (result.getStatus() == Response.STATUS_OK) {
			loginUsers.add(user);
		}
		res.getWriter().write(result.getMessage());
	}
	
	
}
