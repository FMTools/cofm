package quiz.server.bean;


public class Answer {

	private int id;
	
	private int quizNo;
	private String choice;
	private long time;
	private int score;
	private User user;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	public void setQuizNo(int quizNo) {
		this.quizNo = quizNo;
	}
	public int getQuizNo() {
		return quizNo;
	}
	public void setChoice(String choice) {
		this.choice = choice;
	}
	public String getChoice() {
		return choice;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getScore() {
		return score;
	}
	
}
