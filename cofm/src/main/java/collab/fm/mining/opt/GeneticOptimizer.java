package collab.fm.mining.opt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class GeneticOptimizer implements Optimizer {
	
	static Logger logger = Logger.getLogger(GeneticOptimizer.class);

	// Population = Max number of solutions in one generation.
	public int population = 50; 
	
	// The proportion of "elites" (best solutions in one generation.)
	public float elite = 0.2f;
	
	// The probability of breeding (combine two solutions into one).
	// Probability of mutation (slightly change a solution) = 1 - breedProb.
	public double breedProb = 0.8;
	
	// The number of generations (iterations).
	public int generation = 100;
	
	private static Random rand = new Random();
	
	private Optimizable target;
	
	public Solution optimize(Optimizable o) {
		logger.debug("[opt] Genetic algorithm.");
		target = o;
		Solution solutionDef = o.defineSolution();
		
		// Generate the first generation randomly.
		Solution[] spieces = new Solution[population];
		for (int i = 0; i < population; i++) {
			Domain[] init = Arrays.copyOf(solutionDef.parts, solutionDef.parts.length);
			for (Domain d: init) {
				d.getInitValue();
			}
			Solution s = new Solution();
			s.parts = init;
			s.cost = o.computeCost(s);
			spieces[i] = s;
		}
		
		int top = Math.round(elite * population);
		
		for (int i = 0; i < generation; i++) {
			Arrays.sort(spieces);
			
			logger.debug("[opt] Generation #" + i + ": Best =  " + spieces[0].toString());
			
			// Keep the top elites, and add new solutions by mutating or crossing-over
			for (int j = top; j < population; j++) {
				if (Math.random() <= breedProb) {
					spieces[j] = crossover(spieces[rand.nextInt(top)], spieces[rand.nextInt(top)]);
				} else {
					spieces[j] = mutate(spieces[rand.nextInt(top)]);
				}
			}
			System.out.print(".");
		}
		System.out.println();
		Arrays.sort(spieces);
		//logger.info("*** Optimization END. Best is " + spieces[0].toString());
		return spieces[0];
	}
	
	private Solution mutate(Solution s) {
		// Slightly change a random domain of the individual.
		Domain[] next = Arrays.copyOf(s.parts, s.parts.length);
		int i = rand.nextInt(s.parts.length);
		if (Math.random() > 0.5) {
			next[i].increase();
		} else {
			next[i].decrease();
		}
		Solution sol = new Solution();
		sol.parts = next;
		sol.cost = target.computeCost(sol);
		return sol;
	}
	
	private Solution crossover(Solution first, Solution second) {
		int i;
		if (first.parts.length <= 2) {
			i = 1;
		} else {
			i = rand.nextInt(first.parts.length - 2) + 1;
		}
		Domain[] next = new Domain[first.parts.length];
		for (int j = 0; j < i; j++) {
			next[j] = first.parts[j];
		}
		for (int k = i; k < second.parts.length; k++) {
			next[k] = second.parts[k];
		}
		Solution sol = new Solution();
		sol.parts = next;
		sol.cost = target.computeCost(sol);
		return sol;
	} 
	
}
