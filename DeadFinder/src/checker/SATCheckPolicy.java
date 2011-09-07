package checker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import model.Clause;
import model.Feature;
import model.Clause.FalseClause;
import model.Clause.TrueClause;

/**
 * Call Sat4j to do real checking.
 * @author Yi Li
 *
 */
public class SATCheckPolicy implements DeadCheckPolicy {

	static Logger logger = Logger.getLogger(SATCheckPolicy.class);
	
	public static final int TIME_OUT = 3600;  // 3600 seconds
	
	public String toString() {
		return "SAT4J";
	}
	
	@Override
	public boolean isDead(Feature feature) {
		// 1. If the feature is not involved in any constraint, it won't be dead.
		// 2. Otherwise, assign "true" to the feature and get the updated clauses, then do the checking
		
		boolean isConstrained = false;
		List<Clause> cons = new ArrayList<Clause>();
		
		for (Clause c: feature.getFm().getConstraints()) {
			Clause assigned = c.assign(true, feature);
			if (assigned == null) {
				// The feature doesn't appear in "c", keep "c" unchanged
				cons.add(c);
			} else {
				isConstrained = true;
				if (assigned instanceof FalseClause) {
					// A false clause always unsatisfies the whole constraints
					return true;
				}
				if (!(assigned instanceof TrueClause)) {
					// We skip the true clause because it has no effect on checking
					cons.add(assigned);
				}
			}
		}
		
		if (!isConstrained) {
			return false;
		}
		
		// Do SAT checking for "cons"
		int maxVar = feature.getFm().getNumFeatures();
		int maxClause = cons.size();
		
		ISolver solver = SolverFactory.newDefault();
		solver.setTimeout(TIME_OUT);
		solver.newVar(maxVar);
		solver.setExpectedNumberOfClauses(maxClause);
		
		for (Clause c: cons) {
			try {
				solver.addClause(new VecInt(c.toCNF()));
			} catch (ContradictionException e) {
				logger.warn("Contradiction in clause: " + c.toString(), e);
			}
		}
		
		IProblem problem = solver;
		try {
			return !problem.isSatisfiable();
		} catch (TimeoutException e) {
			logger.error("Solver timeout when checking the feature '" + feature.getName() + "'", e);
			return false;
		}
	}
	

}
