package collab.fm.server.bean.transfer;

import java.util.HashSet;
import java.util.Set;

public class VotableElement2 extends DataItem2 {
	// Voters of the YES vote (1-vote)
	protected Set<Long> v1 = new HashSet<Long>();
	// Voters of the NO vote (0-vote)
	protected Set<Long> v0 = new HashSet<Long>();
	// Viewers
	protected Set<Long> viewer = new HashSet<Long>();
	// View counts
	protected Long viewCount;
	
	protected Long typeId;
	
	public Set<Long> getV1() {
		return v1;
	}
	public void setV1(Set<Long> v1) {
		this.v1 = v1;
	}
	public Set<Long> getV0() {
		return v0;
	}
	public void setV0(Set<Long> v0) {
		this.v0 = v0;
	}
	public void addV1(Long id) {
		v1.add(id);
	}
	public void addV0(Long id) {
		v0.add(id);
	}
	public void addViewer(Long id) {
		viewer.add(id);
	}
	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	public Set<Long> getViewer() {
		return viewer;
	}
	public void setViewer(Set<Long> viewer) {
		this.viewer = viewer;
	}
	public Long getViewCount() {
		return viewCount;
	}
	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}
	
}
