package collab.fm.server.bean.entity.attr;

import java.util.*;

import collab.fm.server.bean.transfer.EnumAttribute2;

/**
 * Attribute of Enumeration type.
 * @author mark
 *
 */
public class EnumAttribute extends Attribute {
	
	private List<String> validValues = new ArrayList<String>();

	public EnumAttribute() {
		super();
	}
	
	public EnumAttribute(Long creator, String name) {
		super(creator, name, Attribute.TYPE_ENUM);
	}
	
	@Override
	protected boolean valueIsValid(Value v) {
		return validValues.contains(v.value());
	}
	
	public void addValidValue(String value) {
		validValues.add(value);
	}
	
	public List<String> getValidValues() {
		return validValues;
	}

	public void setValidValues(List<String> validValues) {
		this.validValues = validValues;
	}
	
	public void transfer(EnumAttribute2 a2) {
		super.transfer(a2);
		for (String s: this.getValidValues()) {
			a2.addEnum(s);
		}
	}
}
