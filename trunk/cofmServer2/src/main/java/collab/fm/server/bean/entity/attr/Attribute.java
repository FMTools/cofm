package collab.fm.server.bean.entity.attr;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.entity.Entity;
import collab.fm.server.bean.transfer.Attribute2;
import collab.fm.server.bean.transfer.Value2;

/**
 * @author mark
 * An attribute is associated with a set of vote-able values; however, the attribute itself is NOT vote-able.
 */
public class Attribute extends Entity {
	// Types
	public static final String TYPE_STR = "string";
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_LIST = "list";
	public static final String TYPE_ENUM = "enum";
	public static final String TYPE_NUMBER = "number";
	
	// Attribute name
	protected String name;
	
	// Attribute type
	protected String type;
	
	protected List<Value> values = new ArrayList<Value>();
	
	// If multipleSupport == true, one user can vote yes to multiple values of this attribute;
	// otherwise, one user can vote yes to up to one value of this attribute.
	protected boolean multipleSupport;
	
	// If enableGlobalDupValues == true, then duplicate values for this attr is enabled (e.g. Optionality),
	// otherwise, no duplicate values are allowed globally in one feature model (e.g. Feature Name)
	protected boolean enableGlobalDupValues;
	
	public Attribute() {
		super();	
	}
	
	public Attribute(Long creator, String name, String type) {
		super(creator);
		this.name = name;
		this.type = type;
		this.multipleSupport = true;
		this.enableGlobalDupValues = true;
	}
	
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

	public List<Value> getValues() {
		return values;
	}

	public void setValues(List<Value> values) {
		this.values = values;
	}

	public boolean isMultipleSupport() {
		return multipleSupport;
	}

	public void setMultipleSupport(boolean multipleSupport) {
		this.multipleSupport = multipleSupport;
	}

	public boolean isEnableGlobalDupValues() {
		return enableGlobalDupValues;
	}

	public void setEnableGlobalDupValues(boolean enableGlobalDupValues) {
		this.enableGlobalDupValues = enableGlobalDupValues;
	}
	
	// return true if the value has already existed.
	public boolean voteOrAddValue(Value value, boolean yes, Long userId) {
		if (!valueIsValid(value)) {
			return false;
		}
		boolean isVoting = false;
		// Check for voting
		for (Value v: values) {
			boolean hasVoted = false;
			if (v.equals(value)) {
				isVoting = true;
				hasVoted = true;
				v.vote(yes, userId);
			} else if (!this.multipleSupport && yes) {
				// If multipleSupport is disabled and this vote is YES, then we auto vote NO to other values
				// (NOTE: if this vote is NO, we do nothing.)
				hasVoted = true;
				v.vote(false, userId);
			}
			if (hasVoted && v.getSupporterNum() <= 0) {
				// If there's no supporters after the vote, then remove this value.
				values.remove(v);
			}
		}
		if (!isVoting) {
			// The value does not exist, we create it here.
			value.vote(true, userId);
			values.add(value);
		}
		return true;
	}
	
	protected boolean valueIsValid(Value v) {
		return true;
	}
	
	public void transfer(Attribute2 a2) {
		super.transfer(a2);
		a2.setDup(this.isEnableGlobalDupValues());
		a2.setMulti(this.isMultipleSupport());
		a2.setName(this.getName());
		a2.setType(this.getType());
		for (Value v: this.getValues()) {
			Value2 v2 = new Value2();
			v.transfer(v2);
			a2.addVal(v2);
		}
	}
}
