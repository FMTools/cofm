package collab.fm.server.bean.entity.attr;

import collab.fm.server.bean.transfer.NumericAttribute2;

public class NumericAttribute extends Attribute {
	
	private double min;
	private double max;
	private String unit;
	
	public NumericAttribute() {
		super();
	}
	
	public NumericAttribute(Long creator, String name) {
		super(creator, name, Attribute.TYPE_NUMBER);
	}
	
	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	protected boolean valueIsValid(Value v) {
		try {
			Double d = Double.valueOf(v.value());
			return min <= d.doubleValue() && max >= d.doubleValue();
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public void transfer(NumericAttribute2 a2) {
		super.transfer(a2);
		a2.setMin(this.getMin());
		a2.setMax(this.getMax());
		a2.setUnit(this.getUnit());
	}

}
