package collab.fm.server.bean.persist.entity;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.transfer.DataItem2;

/**
 * @author mark
 * Definition of an String-Like (String or Text) attribute type
 */
public class AttributeType extends DataItem {
	// Types
	public static final String TYPE_STR = "string";
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_ENUM = "enum";
	public static final String TYPE_NUMBER = "number";

	protected EntityType hostType;
	
	protected String attrName;
	
	protected String typeName;
	
	// If multipleSupport == true, one user can vote yes to multiple values of this attribute;
	// otherwise, one user can vote yes to up to one value of this attribute.
	protected boolean multipleSupport;
	
	// If enableGlobalDupValues == true, then duplicate values for this attr is enabled (e.g. Optionality),
	// otherwise, no duplicate values are allowed globally in one feature model (e.g. Feature Name)
	protected boolean enableGlobalDupValues;
	
	@Override
	public void transfer(DataItem2 a) {
//		Attribute2 a2 = (Attribute2) a;
//		super.transfer(a2);
//		a2.setDup(this.isEnableGlobalDupValues());
//		a2.setMulti(this.isMultipleSupport());
//		a2.setName(this.getName());
//		a2.setType(this.getType());
//		for (Value v: this.getValues()) {
//			Value2 v2 = new Value2();
//			v.transfer(v2);
//			a2.addVal(v2);
//		}
	}
	
	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return this.typeName + (this.multipleSupport ? "1" : "0") +
			(this.enableGlobalDupValues ? "1" : "0");
	}
	
	public boolean valueConformsToType(String v) {
		// A value is always a String, so we return true directly here.
		return true;
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

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public EntityType getHostType() {
		return hostType;
	}

	public void setHostType(EntityType hostType) {
		this.hostType = hostType;
	}

	
}
