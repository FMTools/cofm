package collab.fm.server.bean.transfer;

public class DataItem2 {
	protected Long id;
	protected Long cid;   // ID of its creator
	protected String ctime; // Date/Time of its creation
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCid() {
		return cid;
	}
	public void setCid(Long cid) {
		this.cid = cid;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
