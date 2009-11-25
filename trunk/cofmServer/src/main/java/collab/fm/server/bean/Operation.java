package collab.fm.server.bean;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import collab.fm.server.util.BeanUtils;

public class Operation {

	protected String name;
	protected Boolean vote;
	protected Long userid; // the committer id
	
	public Operation() {
		
	}
	
	public List<Operation> apply() throws RuntimeException {
		return null;
	}
	
	public String toString() {
		return name + " " + vote + " " + userid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String op) {
		this.name = op;
	}

	public Boolean getVote() {
		return vote;
	}

	public void setVote(Boolean vote) {
		this.vote = vote;
	}
	
	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}
}
