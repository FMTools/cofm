package quiz.server.bean;

public class Candidate {

	private int id;
	
	private Quiz quiz;
	private String content;
	
	private boolean correct;

	
	@Override
	public boolean equals(Object that) {
		if (this == that) return true;
		if (that == null) return false;
		if (!(that instanceof Candidate)) return false;
		Candidate c = (Candidate) that;
		return (this.content == null && c.getContent() == null) ? this.id == c.getId() : 
			this.content == c.getContent();
	}
	
	@Override
	public int hashCode() {
		if (content == null) {
			return id;
		}
		return content.hashCode();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public Quiz getQuiz() {
		return quiz;
	}
	
	
}
