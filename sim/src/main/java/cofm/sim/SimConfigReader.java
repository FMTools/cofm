package cofm.sim;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import cofm.sim.agent.Agent;
import cofm.sim.agent.CofmAgent;
import cofm.sim.agent.behavior.SelectionPolicy;
import cofm.sim.element.FmElement;
import cofm.sim.limiter.EmptyLimiter;
import cofm.sim.limiter.Limiter;
import cofm.sim.limiter.risk.AgentRiskSharePolicy;
import cofm.sim.limiter.risk.ElementRiskPolicy;
import cofm.sim.pool.CofmPool;
import cofm.sim.pool.EndCondition;
import cofm.sim.pool.Pool;
import cofm.sim.pool.future.Future;

public class SimConfigReader {

	static Logger logger = Logger.getLogger(SimConfigReader.class);
	
	private static final String AGENT = "AGENT";
	
	private static final String LIMITER = "LIMITER";
	private static final String LIMITER_RISK_CLASS = "cofm.sim.limiter.risk.RiskLimiter";
	private static final String LIMITER_EXP_RISK_CLASS = "cofm.sim.limiter.risk.ExpRiskPolicy";
	
	private static final String END = "END";
	private static final String START = "START";
	
	private static final String FUTURE = "FUTURE";
	private static final String EACH = "EACH";
	private static final String ONCE = "ONCE";
	
	public Pool initEnvironment(String file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			Pool pool = new CofmPool();
			Limiter limiter = null;
			int id = 1;
			String s;
			while ((s = in.readLine()) != null) {
				if (s.trim().length() <= 0 
						|| s.startsWith("#")) {  // "#" is a comment line
					continue;
				}
				
				if (s.startsWith(LIMITER)) {
					limiter = parseLimiter(s, pool);
					pool.setLimiter(limiter);
				} else if (s.startsWith(AGENT)) {
					id += parseAgents(s, id, pool, limiter);
				} else if (s.startsWith(END)) {
					setEndCondition(s, pool);
				} else if (s.startsWith(START)) {
					setStartCondition(s, pool);
				} else if (s.startsWith(FUTURE)) {
					parseFuture(s, pool, limiter);
				}
				
			}
			
			in.close();
			
			logger.info(pool.toString());
			
			return pool;
		} catch (FileNotFoundException e) {
			logger.warn("Fail to open Sim-Def file.", e);
			return null;
		} catch (IOException e) {
			logger.warn("Fail to read Sim-Def file.", e);
			return null;
		}
		
	}

	private void parseFuture(String s, Pool pool, Limiter limiter) {
		String[] parts = s.split(" ");
		int i = 1;
		
		String m = parts[i++];
		int mode = (m == EACH ? Future.MODE_REPEAT : Future.MODE_ONCE);
		
		int turn = Integer.valueOf(parts[i++]);
		
		try {
			Class<?> cls = Class.forName(parts[i++]);
			
			// Now we handle FutureAgent only.
			Constructor<?> ctor = cls.getConstructor(Pool.class, Integer.class, Integer.class, Integer.class, Agent.class);
			
			String name = parts[i++];
			
			int num = Integer.valueOf(parts[i++]);
			
			double minRating = Double.valueOf(parts[i++]);
			double maxRating = Double.valueOf(parts[i++]);
			double probCreate = Double.valueOf(parts[i++]);
			double probSelect = Double.valueOf(parts[i++]);
			double probDeselect = Double.valueOf(parts[i++]);

			String spc = parts[i++];
			Class<?> selectionPolicyClass = Class.forName(spc);
			Constructor<?> spctor = selectionPolicyClass.getConstructor(Pool.class, Double.class, Double.class);

			double par1 = Double.valueOf(parts[i++]);
			double par2 = Double.valueOf(parts[i++]);

			SelectionPolicy sp = (SelectionPolicy) spctor.newInstance(pool, par1, par2);

			Agent agent = new CofmAgent(pool, limiter, 0, name,
					minRating, maxRating,
					probCreate, probSelect, probDeselect, 
					sp);
			
			Future future = (Future) ctor.newInstance(pool, mode, turn, num, agent);
			pool.addFutureEvent(future);
		} catch (Exception e) {
			logger.warn("Fail to parse Future.", e);
		}
	}

	private void setStartCondition(String s, Pool pool) {
		String[] parts = s.split(" ");
		int i = 1;
		double rating = Double.valueOf(parts[i++]);
		pool.addElement(new FmElement(null, 0, rating));
	}

	private void setEndCondition(String s, Pool pool) {
		String[] parts = s.split(" ");
		int i = 1;
		
		try {
			Class<?> ec = Class.forName(parts[i++]);
			Constructor<?> ctor = ec.getConstructor(Pool.class, Integer.class);
			
			int val = Integer.valueOf(parts[i++]);
			EndCondition cond = (EndCondition) ctor.newInstance(pool, val);
			pool.setEndCondition(cond);
			
		} catch (Exception e) {
			logger.warn("Fail to set End Condition.", e);
		}
		
	}

	private int parseAgents(String s, int beginId, Pool pool, Limiter limiter) {
		String[] parts = s.split(" ");
		int i = 1;
		
		String name = parts[i++];
		
		int num = Integer.valueOf(parts[i++]);
		
		double minRating = Double.valueOf(parts[i++]);
		double maxRating = Double.valueOf(parts[i++]);
		double probCreate = Double.valueOf(parts[i++]);
		double probSelect = Double.valueOf(parts[i++]);
		double probDeselect = Double.valueOf(parts[i++]);
		
		String spc = parts[i++];
		try {
			Class<?> selectionPolicyClass = Class.forName(spc);
			Constructor<?> spctor = selectionPolicyClass.getConstructor(Pool.class, Double.class, Double.class);
			
			double par1 = Double.valueOf(parts[i++]);
			double par2 = Double.valueOf(parts[i++]);
			
			SelectionPolicy sp = (SelectionPolicy) spctor.newInstance(pool, par1, par2);
			
			for (int j = 0; j < num; j++) {
				Agent agent = new CofmAgent(pool, limiter, beginId++, name,
						minRating, maxRating,
						probCreate, probSelect, probDeselect, 
						sp);
				if (j == 0) { // Add an agent to the tracker
					pool.addToTracker(agent);
				}
			}
		} catch (Exception e) {
			logger.warn("Fail to create Selection Policy.", e);
		}
		return num;
	}

	private Limiter parseLimiter(String s, Pool pool) {
		String[] parts = s.split(" ");
		
		// The limiter class
		String limiterClass = parts[1];
		
		if (limiterClass.equals(LIMITER_RISK_CLASS)) {
			return initRiskLimiter(parts, pool);
		}
		
		return new EmptyLimiter();
	}

	private Limiter initRiskLimiter(String[] parts, Pool pool) {
		try {
			int i = 1;
			Class<?> limiterClass = Class.forName(parts[i++]);
			Constructor<?> limiterCtor = limiterClass.getConstructor(Pool.class, Double.class, Double.class, ElementRiskPolicy.class, AgentRiskSharePolicy.class);
			
			double maxRisk = Double.valueOf(parts[i++]);
			double rateThresh = Double.valueOf(parts[i++]);
			
			ElementRiskPolicy riskPolicy = null;
			String rpc = parts[i++]; 
			Class<?> riskPolicyClass = Class.forName(rpc);
			if (rpc.equals(LIMITER_EXP_RISK_CLASS)) {
				Constructor<?> rpcCtor = riskPolicyClass.getConstructor(Double.class, Integer.class);
				double thresh = Double.valueOf(parts[i++]);
				int steepness = Integer.valueOf(parts[i++]);
				
				riskPolicy = (ElementRiskPolicy) rpcCtor.newInstance(thresh, steepness);
			} else {
				Constructor<?> rpcCtor = riskPolicyClass.getConstructor(Double.class);
				double thresh = Double.valueOf(parts[i++]);
				
				riskPolicy = (ElementRiskPolicy) rpcCtor.newInstance(thresh);
			}
			
			Class<?> sharePolicyClass = Class.forName(parts[i++]);
			AgentRiskSharePolicy sharePolicy = (AgentRiskSharePolicy) sharePolicyClass.newInstance();
			
			return (Limiter) limiterCtor.newInstance(pool, maxRisk, rateThresh, riskPolicy, sharePolicy);
			
		} catch (Exception e) {
			logger.warn("Fail to create Limiter.", e);
			return null;
		}
		
		
	}
}
