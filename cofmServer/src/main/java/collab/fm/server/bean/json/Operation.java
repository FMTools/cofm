package collab.fm.server.bean.json;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import collab.fm.server.util.BeanUtils;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;

public class Operation {

	protected String name;
	protected Boolean vote;
	protected Long userid; // the committer id
	
	public Operation() {
		
	}
	
	protected boolean isFieldValid() {
		return name != null && vote != null && userid != null;
	}
	
	/**
	 * Apply an operation on a feature model. When a valid operation is applied,
	 * the following things happens:<br/>
	 *  1) Implied operations of the origin operation are deduced. (throws InvalidOperationException)<br/>
	 *  2) These operations are applied on corresponding feature model. (throws InvalidOperationException)<br/>
	 *  3) The changed feature model is persisted. (throws BeanPersistenceException)<br/>
	 *  4) Null fields of the origin operation are set, e.g. the featureId of a "Create Feature Operation"<br/>
	 *  5) Returns the (modified) origin operation.<br/>
	 *  
	 * @return The origin operation whose null fields were set during persistence.
	 * @throws BeanPersistenceException If the changes made by the operation can't be persisted.
	 * @throws InvalidOperationException If the operation can't be analyzed and applied correctly. 
	 * @throws OperationNotSupportedException 
	 */
	public Operation apply() throws BeanPersistenceException, InvalidOperationException, OperationNotSupportedException {
		throw new OperationNotSupportedException();
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
