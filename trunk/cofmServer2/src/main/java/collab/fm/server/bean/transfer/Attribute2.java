package collab.fm.server.bean.transfer;

import java.util.ArrayList;
import java.util.List;

public class Attribute2 extends DataItem2 {
	protected String name;
	protected String type;
	protected boolean multi;
	protected boolean dup; // global duplicate
	protected List<Value2> vals = new ArrayList<Value2>();
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
	public List<Value2> getVals() {
		return vals;
	}
	public void setVals(List<Value2> vals) {
		this.vals = vals;
	}
	
	public void addVal(Value2 v) {
		this.vals.add(v);
	}
	
}
