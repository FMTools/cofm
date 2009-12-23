package collab.fm.server.bean.protocol;

import java.util.List;
import java.util.Set;

public class UpdateResponse extends Response {

	private List<Feature2> features;
	private List<BinaryRelation2> binaries;
	
	public List<Feature2> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature2> features) {
		this.features = features;
	}

	public List<BinaryRelation2> getBinaries() {
		return binaries;
	}

	public void setBinaries(List<BinaryRelation2> binaries) {
		this.binaries = binaries;
	}

	public boolean valid() {
		return super.valid();
	}
	
	public static class Name2 {
		private String val;
		private Set<Long> uYes;
		private Set<Long> uNo;
		
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public Set<Long> getuYes() {
			return uYes;
		}
		public void setuYes(Set<Long> uYes) {
			this.uYes = uYes;
		}
		public Set<Long> getuNo() {
			return uNo;
		}
		public void setuNo(Set<Long> uNo) {
			this.uNo = uNo;
		}
	}
	
	public static class Des2 {
		private String val;
		private Set<Long> uYes;
		private Set<Long> uNo;
		
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public Set<Long> getuYes() {
			return uYes;
		}
		public void setuYes(Set<Long> uYes) {
			this.uYes = uYes;
		}
		public Set<Long> getuNo() {
			return uNo;
		}
		public void setuNo(Set<Long> uNo) {
			this.uNo = uNo;
		}
	}
	
	public static class Feature2 {
		private Long id;

		private List<Name2> name;
		private List<Des2> des;
		// user vote YES/NO for this feature
		private Set<Long> uYes;
		private Set<Long> uNo;
		// user vote YES/NO for optionality 
		private Set<Long> uOptYes;
		private Set<Long> uOptNo;
		
		private List<Long> rels; // relationships
		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		public List<Name2> getName() {
			return name;
		}

		public void setName(List<Name2> name) {
			this.name = name;
		}

		public List<Des2> getDes() {
			return des;
		}

		public void setDes(List<Des2> des) {
			this.des = des;
		}

		public Set<Long> getuYes() {
			return uYes;
		}

		public void setuYes(Set<Long> uYes) {
			this.uYes = uYes;
		}

		public Set<Long> getuNo() {
			return uNo;
		}

		public void setuNo(Set<Long> uNo) {
			this.uNo = uNo;
		}

		public Set<Long> getuOptYes() {
			return uOptYes;
		}

		public void setuOptYes(Set<Long> uOptYes) {
			this.uOptYes = uOptYes;
		}

		public Set<Long> getuOptNo() {
			return uOptNo;
		}

		public void setuOptNo(Set<Long> uOptNo) {
			this.uOptNo = uOptNo;
		}

		public List<Long> getRels() {
			return rels;
		}

		public void setRels(List<Long> rels) {
			this.rels = rels;
		}
	}
	
	public static class BinaryRelation2 {
		private Long id;
		private String type;
		private Set<Long> uYes;
		private Set<Long> uNo;
		private Long left;
		private Long right;
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Set<Long> getuYes() {
			return uYes;
		}
		public void setuYes(Set<Long> uYes) {
			this.uYes = uYes;
		}
		public Set<Long> getuNo() {
			return uNo;
		}
		public void setuNo(Set<Long> uNo) {
			this.uNo = uNo;
		}
		public Long getLeft() {
			return left;
		}
		public void setLeft(Long left) {
			this.left = left;
		}
		public Long getRight() {
			return right;
		}
		public void setRight(Long right) {
			this.right = right;
		}
	}
	
}
