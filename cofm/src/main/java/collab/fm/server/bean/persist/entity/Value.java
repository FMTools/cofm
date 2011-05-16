package collab.fm.server.bean.persist.entity;

import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.Value2;

/**
 * All types of value (string, number, enumeration, etc.) can be converted into string, so
 * we use a single Value class for all types of values.
 * @author mark
 *
 */
public class Value extends Element implements Comparable<Value> {

	private String val;
	
	public static final String SINGLE_QUOTE = "_squote_";
	public static final String DOUBLE_QUOTE = "_dquote_";
	
	public Value() {
		super();
	}
	
	public String getVal() {
		return val;
	}

	public void setVal(String strVal) {
		this.val = strVal.trim().replaceAll("\"", DOUBLE_QUOTE).replaceAll("'", SINGLE_QUOTE);
	}

	@Override
	public String toValueString() {
		return getVal();
	}

	@Override
	public void transfer(DataItem2 v) {
		Value2 v2 = (Value2) v;
		super.transfer(v2);
		v2.setVal(this.getVal());
	}

	public int compareTo(Value o) {
		return new Float(this.getSupportRate()).compareTo(new Float(o.getSupportRate()));
	}

}
