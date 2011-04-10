package collab.fm.mining.opt;

public interface Optimizable {

	// The cost function, calculate the cost of solution.
	public double computeCost(Solution s);
	
	// Define what is a valid solution (an array of Domain(low, high, step))
	public Solution defineSolution();
	
}
