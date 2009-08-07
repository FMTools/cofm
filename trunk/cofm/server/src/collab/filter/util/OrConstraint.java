package collab.filter.util;

public class OrConstraint implements Constraint{
	private Constraint cst1;
	private Constraint cst2;
	
	public OrConstraint(Constraint cst1, Constraint cst2) {
		this.cst1 = cst1;
		this.cst2 = cst2;
	}
	
	public boolean conformTo(Object obj) {
		return cst1.conformTo(obj) || cst2.conformTo(obj);
	}
	
	@Override
	public String toString() {
		return "( " + cst1.toString() + " ) OR ( " + cst2.toString() + " )"; 
	}
}
