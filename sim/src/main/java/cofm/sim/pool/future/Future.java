package cofm.sim.pool.future;

public interface Future {

	// Change ONCE or REPEATLY 
	public static final int MODE_REPEAT = 1;
	public static final int MODE_ONCE = 2; 
	
	void changePool();
}
