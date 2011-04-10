package collab.fm.mining.opt;

public class Domain {
	public double low;
	public double high;
	public double value;  // low <= value <= high
	public double step;
	
	public Domain() {
		
	}
	
	public Domain(double lo, double hi, double stp) {
		this.low = lo;
		this.high = hi;
		this.step = stp;
	}
	
	public void randomValue() {
		this.value = Math.random() * (high - low) + low;   // A value between low and high
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
