package collab.fm.server.bean.persist;

import java.util.Date;

import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.util.EntityUtil;

/**
 * The root class for all things that needs to be persisted in database.
 * @author mark
 *
 */
public abstract class DataItem {
	protected Long id;
	protected Long creator;
	
	protected Date createTime;
	protected Date lastModifyTime; 
	
	public DataItem() {
		this.setCreateTime(new Date());
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
	
	public void transfer(Entity2 target) {
		target.setId(this.getId());
		target.setCid(this.getCreator());
		target.setCtime(EntityUtil.formatDate(this.getCreateTime()));
	}
}
