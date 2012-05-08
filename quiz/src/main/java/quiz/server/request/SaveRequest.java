package quiz.server.request;

import quiz.server.bean.Answer;
import quiz.server.bean.Candidate;
import quiz.server.bean.User;
import quiz.server.dao.DaoUtil;

public class SaveRequest implements Request {

	private String name;
	private int quizId;
	private int answer;
	private long time;
	
	public SaveRequest(String name, int quizId, int answer, long time) {
		this.name = name;
		this.quizId = quizId;
		this.answer = answer;
		this.time = time;
	}
	
	public Response handle() {
		User u = DaoUtil.getUserDao().getByName(name);
		if (u == null) {
			return new Response(Response.STATUS_BAD, "No such user.");
		}
		
		for (Answer a: u.getAnswers()) {
			if (quizId == a.getChoice().getQuiz().getId()) {
				return new Response(Response.STATUS_BAD, "Quiz has already been answered.");
			}
		}
		
		Candidate choice = DaoUtil.getCandidateDao().getById(answer, false);
		if (choice == null) {
			return new Response(Response.STATUS_BAD, "No such candidate.");
		}
		
		if (choice.getQuiz().getId() != this.quizId) {
			return new Response(Response.STATUS_BAD, "Candidate and Quiz don't match.");
		}
		
		Answer answer = new Answer();
		answer.setChoice(choice);
		answer.setTime(time);
		
		u.getAnswers().add(answer);
		
		DaoUtil.getAnswerDao().save(answer);
		DaoUtil.getUserDao().save(u);
		
		return new Response(Response.STATUS_OK);
	}

}
