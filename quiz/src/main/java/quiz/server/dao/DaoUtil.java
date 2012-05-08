package quiz.server.dao;

import quiz.server.bean.Answer;
import quiz.server.bean.Candidate;
import quiz.server.bean.Quiz;

public class DaoUtil {

	public static UserDao getUserDao() {
		return new UserDaoImpl();
	}
	
	public static CandidateDao getCandidateDao() {
		return new CandidateDaoImpl();
	}
	
	public static AnswerDao getAnswerDao() {
		return new AnswerDaoImpl();
	}
	
	public static QuizDao getQuizDao() {
		return new QuizDaoImpl();
	}
	
	static class CandidateDaoImpl extends GenericDaoImpl<Candidate> implements CandidateDao { }
	static class AnswerDaoImpl extends GenericDaoImpl<Answer> implements AnswerDao { }
	static class QuizDaoImpl extends GenericDaoImpl<Quiz> implements QuizDao { }
}
