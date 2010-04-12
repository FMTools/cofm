package collab.fm.server.bean.entity;

import java.util.Date;

public class VersionedEntity {
	protected Date created;
	protected Date lastUpdated;
	
	public VersionedEntity() {
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
