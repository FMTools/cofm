package collab.fm.server.bean.transfer;

import java.util.ArrayList;
import java.util.List;

public class ValueList2 {
	private Long attrId;
	private List<Value2> vals = new ArrayList<Value2>();
	public Long getAttrId() {
		return attrId;
	}
	public void setAttrId(Long attrId) {
		this.attrId = attrId;
	}
	public List<Value2> getVals() {
		return vals;
	}
	public void setVals(List<Value2> vals) {
		this.vals = vals;
	}
	
}
