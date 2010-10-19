package collab.fm.server.bean.persist.entity;

import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.Value2;

/**
 * All types of value (string, number, enumeration, etc.) can be converted into string, so
 * we use a single Value class for all types of values.
 * @author mark
 *
 */
public class Value extends Element {

	private String val;
	
	public Value() {
		super();
	}
	
	public String getVal() {
		return val;
	}

	public void setVal(String strVal) {
		this.val = strVal.trim();
	}

	public String toValueString() {
		return getVal();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (this == null || o == null) return false;
		if (!(o instanceof Value)) return false;
		Value that = (Value) o;
		return this.toValueString().equals(that.toValueString());
	}

	@Override
	public int hashCode() {
		return this.toValueString().hashCode();
	}
	
	@Override
	public void transfer(Entity2 v) {
		Value2 v2 = (Value2) v;
		super.transfer(v2);
		v2.setVal(this.getVal());
	}

}
