package collab.fm.server.bean.transfer;

import java.util.Set;

public class VotableString {
	private String val;
	
	/**
	 * v1 means "vote score 1", score=1 indicates this is a "YES" vote, 
	 * I use "one" instead of "yes" to support possible rating mechanism in the future.
	 */
	private Set<Long> v1;
	
	/**
	 * v0 means vote NO
	 */
	private Set<Long> v0;
	
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
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
	
	
}
