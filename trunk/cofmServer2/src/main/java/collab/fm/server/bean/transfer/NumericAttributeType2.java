package collab.fm.server.bean.transfer;

public class NumericAttributeType2 extends AttributeType2 {
	private float min;
	private float max;
	private String unit;
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
