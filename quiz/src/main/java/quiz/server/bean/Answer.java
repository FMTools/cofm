package quiz.server.bean;


public class Answer {

	private int id;
	
	private Candidate choice;
	private long time;
	
	private User user;
	
	public int calcPoint() {
		if (choice.isCorrect()) {
			return choice.getQuiz().getPoint();
		}
		return 0;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Candidate getChoice() {
		return choice;
	}
	public void setChoice(Candidate choice) {
		this.choice = choice;
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
	
}
