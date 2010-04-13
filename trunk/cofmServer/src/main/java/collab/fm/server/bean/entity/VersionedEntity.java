package collab.fm.server.bean.entity;

import java.util.Date;

public class VersionedEntity {
	protected Date created;
	protected Date lastUpdated;
	protected Long creator;

	public VersionedEntity(Long creatorId) {
		creator = creatorId;
		created = new Date();
	}

	public Long getCreator() {
		return creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public VersionedEntity() {
		creator = -1L;
		created = new Date();
	}
	
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
