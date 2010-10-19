package collab.fm.server.bean.persist.entity;

import collab.fm.server.bean.transfer.Attribute2;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.NumericAttribute2;

public class NumericAttributeType extends AttributeType {
	
	private float min;
	private float max;
	private String unit;
	
	public NumericAttributeType() {
		super();
		this.typeName = AttributeType.TYPE_NUMBER;
	}
	
	@Override
	public void transfer(Entity2 a) {
//		NumericAttribute2 a2 = (NumericAttribute2) a;
//		super.transfer(a2);
//		a2.setMin(this.getMin());
//		a2.setMax(this.getMax());
//		a2.setUnit(this.getUnit());
	}
	
	@Override
	public boolean valueConformsToType(Value v) {
		try {
			Float val = Float.valueOf(v.toValueString());
			return !val.isNaN() &&
				val.compareTo(min) >= 0 &&
				val.compareTo(max) <= 0;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
