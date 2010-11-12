package collab.fm.server.bean.transfer;

public class AttributeType2 extends DataItem2 {
	protected Long hostId;
	protected String name;
	protected String type;
	protected boolean multi;
	protected boolean dup;
	public Long getHostId() {
		return hostId;
	}
	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isMulti() {
		return multi;
	}
	public void setMulti(boolean multi) {
		this.multi = multi;
	}
	public boolean isDup() {
		return dup;
	}
	public void setDup(boolean dup) {
		this.dup = dup;
	}
	
}
