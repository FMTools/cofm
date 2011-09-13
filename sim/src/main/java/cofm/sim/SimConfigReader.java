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
import cofm.sim.limiter.EmptyLimiter;
import cofm.sim.limiter.Limiter;
import cofm.sim.limiter.risk.AgentRiskSharePolicy;
import cofm.sim.limiter.risk.ElementRiskPolicy;
import cofm.sim.pool.CofmPool;
import cofm.sim.pool.EndCondition;
import cofm.sim.pool.Pool;

public class SimConfigReader {

	static Logger logger = Logger.getLogger(SimConfigReader.class);
	
	private static final String AGENT = "AGENT";
	
	private static final String LIMITER = "LIMITER";
	private static final String LIMITER_RISK_CLASS = "cofm.sim.limiter.risk.RiskLimiter";
	private static final String LIMITER_EXP_RISK_CLASS = "cofm.sim.limiter.risk.ExpRiskPolicy";
	
	private static final String END = "END";
	
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
					id = parseAgents(s, id, pool, limiter);
				} else if (s.startsWith(END)) {
					setEndCondition(s, pool);
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
		
		int num = Integer.valueOf(parts[i++]);
		
		double minMoral = Double.valueOf(parts[i++]);
		double maxMoral = Double.valueOf(parts[i++]);
		double minTalent = Double.valueOf(parts[i++]);
		double maxTalent = Double.valueOf(parts[i++]);
		double probCreate = Double.valueOf(parts[i++]);
		double probSelect = Double.valueOf(parts[i++]);
		
		String spc = parts[i++];
		try {
			Class<?> selectionPolicyClass = Class.forName(spc);
			Constructor<?> spctor = selectionPolicyClass.getConstructor(Pool.class, Double.class);
			
			double par = Double.valueOf(parts[i++]);
			
			SelectionPolicy sp = (SelectionPolicy) spctor.newInstance(pool, par);
			
			for (int j = 0; j < num; j++) {
				new CofmAgent(pool, limiter, beginId++, 
						minMoral, maxMoral, minTalent, maxTalent,
						probCreate, probSelect, sp);
			}
		} catch (Exception e) {
			logger.warn("Fail to create Selection Policy.", e);
		}
		return beginId;
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
			Constructor<?> limiterCtor = limiterClass.getConstructor(Pool.class, Double.class, ElementRiskPolicy.class, AgentRiskSharePolicy.class);
			
			double maxRisk = Double.valueOf(parts[i++]);
			
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
			
			return (Limiter) limiterCtor.newInstance(pool, maxRisk, riskPolicy, sharePolicy);
			
		} catch (Exception e) {
			logger.warn("Fail to create Limiter.", e);
			return null;
		}
		
		
	}
}
