package collab.fm.server.bean.entity.attr;

import collab.fm.server.bean.entity.VotableEntity;
import collab.fm.server.bean.transfer.Value2;

/**
 * All types of value (string, number, enumeration, etc.) can be converted into string, so
 * we use a single Value class for all values.
 * @author mark
 *
 */
public class Value extends VotableEntity {

	private String strVal;
	
	public Value() {
		super();
	}
	
	public Value(Long creator) {
		super(creator);
	}
	
	public String getStrVal() {
		return strVal;
	}

	public void setStrVal(String strVal) {
		this.strVal = strVal;
	}

	public String value() {
		return strVal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (this == null || o == null) return false;
		if (!(o instanceof Value)) return false;
		Value that = (Value) o;
		return this.value().equals(that.value());
	}

	@Override
	public int hashCode() {
		return this.value().hashCode();
	}

	@Override
	protected void removeThis() {
		// TODO Auto-generated method stub
		
	}
	
	public void transfer(Value2 v2) {
		super.transfer(v2);
		v2.setVal(this.getStrVal());
	}

}
