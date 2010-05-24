package collab.fm.server.bean.transfer;

import java.util.List;
import java.util.Set;

public class Feature2 {
	private Long id;
	private Long cid;
	private List<VotableString> names;
	private List<VotableString> dscs;
	private Set<Long> v1;
	private Set<Long> v0;
	private Set<Long> opt1;
	private Set<Long> opt0;
	
	private Set<Long> rels;

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

	public List<VotableString> getNames() {
		return names;
	}

	public void setNames(List<VotableString> names) {
		this.names = names;
	}

	public List<VotableString> getDscs() {
		return dscs;
	}

	public void setDscs(List<VotableString> dscs) {
		this.dscs = dscs;
	}

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

	public Set<Long> getOpt1() {
		return opt1;
	}

	public void setOpt1(Set<Long> opt1) {
		this.opt1 = opt1;
	}

	public Set<Long> getOpt0() {
		return opt0;
	}

	public void setOpt0(Set<Long> opt0) {
		this.opt0 = opt0;
	}

	public Set<Long> getRels() {
		return rels;
	}

	public void setRels(Set<Long> rels) {
		this.rels = rels;
	}
	
	
}
