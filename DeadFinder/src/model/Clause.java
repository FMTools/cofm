package model;

import java.util.ArrayList;
import java.util.List;

public class Clause {

	public static class FalseClause extends Clause {
		public String toString() {
			return "FALSE";
		}
	}
	
	public static class TrueClause extends Clause {
		public String toString() {
			return "TRUE";
		}
	}
	
	private List<Unit> units = new ArrayList<Unit>();
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < units.size(); i++) {
			sb.append((i == 0 ? "" : ", ") + (units.get(i).isPositive() ? "" : "~") + units.get(i).getVariable().getName());
		}
		return sb.toString();
	}
	
	// Assign "value" to "var", if the var is not inside the clause, return null
	public Clause assign(boolean value, Feature var) {
		boolean foundVar = false;
		Clause c = new Clause();
		for (Unit u: units) {
			if (u.getVariable().equals(var)) {
				foundVar = true;
				if (u.isPositive() == value) {
					return new TrueClause();
				} 
			} else {
				c.addUnit(u.isPositive(), u.getVariable());
			}
		}
		if (foundVar && c.isEmpty()) {
			return new FalseClause();
		}
		if (!foundVar) {
			return null;
		}
		return c;
	}
	
	public void addUnit(boolean positive, Feature var) {
		units.add(new Unit(positive, var));
	}
	
	public boolean isEmpty() {
		return units.isEmpty();
	}
	
	public int[] toCNF() {
		int[] cnf = new int[units.size()];
		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);
			cnf[i] = (unit.isPositive() ? 1 : -1) * unit.getVariable().getId();
		}
		return cnf;
	}
	
	public static class Unit {
		private boolean positive;
		private Feature variable;
		
		public Unit(boolean positive, Feature var) {
			this.positive = positive;
			this.variable = var;
		}
		
		public void setPositive(boolean positive) {
			this.positive = positive;
		}
		public boolean isPositive() {
			return positive;
		}
		public void setVariable(Feature variable) {
			this.variable = variable;
		}
		public Feature getVariable() {
			return variable;
		}
	}
	
}
