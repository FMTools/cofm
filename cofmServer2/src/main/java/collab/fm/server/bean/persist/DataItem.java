package collab.fm.server.bean.persist;

import java.util.Date;

import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.util.EntityUtil;

/**
 * The root class for all things that needs to be persisted in database.
 * @author mark
 *
 */
public abstract class DataItem {
	
	//Return code for operations performed on the item.
	public static final int CREATION_EXECUTED = 0;  // (Valid) creating operation.
	public static final int VOTE_EXECUTED = 1;     // (Valid) voting operation.
	public static final int REMOVAL_EXECUTED = 2;  // Remove via voting.
	public static final int INVALID_OPERATION = -1;
	public static final int EMPTY_OPERATION = -2;
	
	protected Long id;
	
	protected Long creator;
	protected Date createTime;
	
	protected Long lastModifier;
	protected Date lastModifyTime; 
	
	public DataItem() {
	}
	
	public void transfer(DataItem2 target) {
		target.setId(this.getId());
		target.setCid(this.getCreator());
		target.setCtime(EntityUtil.formatDate(this.getCreateTime()));
	}
	
	abstract public String toValueString();
	
	public String toString() {
		return toValueString();
	}
	
	public boolean equals(Object v) {
		if (this == v) return true;
		if (this == null || v == null) return false;
		if (!(v instanceof DataItem)) return false;
		final DataItem that = (DataItem) v;
		return that.toValueString().equals(this.toValueString());
	}
	
	public int hashCode() {
		return this.toValueString().hashCode();
	}
	
	public Long getId() {
		return id;
	}
	protected void setId(Long id) {
		this.id = id;
	}
	public Long getCreator() {
		return creator;
	}
	public void setCreator(Long creator) {
		this.creator = creator;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getLastModifyTime() {
		return lastModifyTime;
	}
	
	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public Long getLastModifier() {
		return lastModifier;
	}

	public void setLastModifier(Long lastModifier) {
		this.lastModifier = lastModifier;
	}
	
}
