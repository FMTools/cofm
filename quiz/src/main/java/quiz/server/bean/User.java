package quiz.server.bean;

import java.util.List;

public class User {

	private int id;
	
	private String name;
	private String vcode;  //validation code
	
	private List<Answer> answers;

	public String getAnsweredQuiz() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		int i = 0;
		for (Answer a: answers) {
			if (i++ > 0) {
				s.append(",");
			}
			s.append(a.getChoice().getQuiz().getId());
		}
		s.append("]");
		return s.toString();
	}
	
	public int calcTotalPoint() {
		int p = 0;
		for (Answer a: answers) {
			p += a.calcPoint();
		}
		return p;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
}
