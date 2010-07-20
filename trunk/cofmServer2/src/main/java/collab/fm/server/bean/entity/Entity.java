package collab.fm.server.bean.entity;

import java.util.Date;

import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.util.EntityUtil;

public abstract class Entity {
	protected Long id;
	protected Long creator;
	
	protected Date createTime;
	protected Date lastModifyTime; 
	
	public Entity() {
		this.setCreateTime(new Date());
	}
	
	public Entity(Long creator) {
		this.setCreateTime(new Date());
		this.setCreator(creator);
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
