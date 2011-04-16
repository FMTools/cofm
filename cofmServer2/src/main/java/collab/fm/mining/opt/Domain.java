package collab.fm.mining.opt;

import java.util.Random;

public class Domain {
	public double low;
	public double high;
	public double value;  // low <= value <= high
	public double step;
	public boolean isInt;
	
	public Domain() {
		
	}
	
	public Domain(boolean isInt, double lo, double hi, double stp) {
		this.isInt = isInt;
		this.low = lo;
		this.high = hi;
		this.step = stp;
	}
	
	public void randomValue() {
		if (isInt) {
			int lo = Double.valueOf(low).intValue();
			int hi = Double.valueOf(high).intValue();
			this.value = new Random().nextInt(hi - lo) + lo;
		} else {
			this.value = Math.random() * (high - low) + low;   // A value between low and high
		}
	}
	
	public void increase() {
		double v = this.value;
		v += step;
		if (v <= high) {
			this.value = v;
		}
	}
	
	public void decrease() {
		double v = this.value;
		v -= step;
		if (v >= low) {
			this.value = v;
		}
	}
}
