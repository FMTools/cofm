package collab.fm.server.bean.persist.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.transfer.Attribute2;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.EnumAttribute2;

/**
 * Attribute of Enumeration type.
 * @author mark
 *
 */
public class EnumAttribute extends Attribute {
	
	private static Logger logger = Logger.getLogger(EnumAttribute.class);
	
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
	
	@Override
	public void transfer(Entity2 a) {
		EnumAttribute2 a2 = (EnumAttribute2) a;
		super.transfer(a2);
		for (String s: this.getValidValues()) {
			a2.addEnum(s);
		}
	}
}
