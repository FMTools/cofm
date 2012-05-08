package quiz.server.bean;

import java.util.Set;

public class Quiz {

	private int id;
	private String content;
	
	private Set<Candidate> candidates;
	
	private int point;
	
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setCandidates(Set<Candidate> candidates) {
		this.candidates = candidates;
	}
	public Set<Candidate> getCandidates() {
		return candidates;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public int getPoint() {
		return point;
	}
	
	
}
