package quiz.server.request;

import org.apache.log4j.Logger;

import quiz.server.bean.Answer;
import quiz.server.bean.Candidate;
import quiz.server.bean.User;
import quiz.server.dao.DaoUtil;

public class SaveRequest implements Request {

	static Logger logger = Logger.getLogger(SaveRequest.class);
	
	private String name;
	private String choice;
	private int quizId;
	private int score;
	private long time;
	
	public SaveRequest(String name, int quizId, String choice, int score, long time) {
		this.name = name;
		this.quizId = quizId;
		this.choice = choice;
		this.score = score;
		this.time = time;
	}
	
	public Response handle() {
		User u = DaoUtil.getUserDao().getByName(name);
		if (u == null) {
			return new Response(Response.STATUS_BAD, "No such user.");
		}
		
		for (Answer a: u.getAnswers()) {
			if (quizId == a.getQuizNo()) {
				return new Response(Response.STATUS_BAD, "Quiz has already been answered.");
			}
		}
		
		Answer answer = new Answer();
		answer.setChoice(choice);
		answer.setTime(time);
		answer.setQuizNo(quizId);
		answer.setScore(score);
		answer.setUser(u);
		
		u.getAnswers().add(answer);
		
		DaoUtil.getAnswerDao().save(answer);
		DaoUtil.getUserDao().save(u);
		
		logger.info("SAVE: " + name + " - Question #" + quizId + " Answer=" + choice + " Score=" + score + " Time=" + time);
		return new Response(Response.STATUS_OK);
	}

}
