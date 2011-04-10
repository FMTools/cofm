package collab.fm.mining.opt;

public class Solution implements Comparable<Solution> {
	public Domain[] parts;
	public double cost;
	public int compareTo(Solution o) {
		return Double.valueOf(cost).compareTo(o.cost);
	}
	
	public String toString() {
		String s = "[";
		for (int i = 0; i < parts.length - 1; i++) {
			s += parts[i].value + ", ";
		}
		s += parts[parts.length - 1].value + "]";
		return "Cost = " + cost + ", Parts = " + s;
	}
}
