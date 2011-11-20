package collab.fm.server.bean.persist.entity;

import java.util.HashSet;
import java.util.Set;

import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.PersonalView;
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
	
	protected Set<PersonalView> views = new HashSet<PersonalView>();  // Selected in many personal views.
	
	public static final String SINGLE_QUOTE = "_squote_";
	public static final String DOUBLE_QUOTE = "_dquote_";
	
	public Value() {
		super();
	}
	
	public String decodeQuotes() {
		return getVal().replaceAll(SINGLE_QUOTE, "\'").replaceAll(DOUBLE_QUOTE, "\"");
	}
	
	public String getVal() {
		return val;
	}

	public void setVal(String strVal) {
		this.val = strVal.trim().replaceAll("\"", DOUBLE_QUOTE).replaceAll("'", SINGLE_QUOTE);
	}

	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
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

	public Set<PersonalView> getViews() {
		return views;
	}

	public void setViews(Set<PersonalView> views) {
		this.views = views;
	}

}
