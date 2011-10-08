package collab.fm.mining.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class Prediction {
	
	static Logger logger = Logger.getLogger(Prediction.class);
	
	private Map<Long, FeaturePair> firstMap = new HashMap<Long, FeaturePair>();
	private Map<Long, FeaturePair> secondMap = new HashMap<Long, FeaturePair>();
	
	public static class Metric {
		private double precision = 0;
		private double recall = 0;
		private int time = 0;
		
		public void push(double precision, double recall) {
			time++;
			this.precision += precision;
			this.recall += recall;
		}
		
		public double avgPrecision() {
			return time == 0 ? 0 : precision / time;
		}
		
		public double avgRecall() {
			return time == 0 ? 0 : recall / time;
		}
		
		public String toString() {
			return "Precision = " + String.format("%.3f", avgPrecision()) +
				", Recall = " + String.format("%.3f", avgRecall());
		}
	}
	
	private Map<Integer, Metric> metrics = new HashMap<Integer, Metric>();
	private double accuracy = 0;
	private double time = 0;
	
	public static double diff(List<FeaturePair> predict1, List<FeaturePair> predict2) {
		if (predict1.size() != predict2.size()) {
			return 1.0;  // Totally different
		}
		int diffnum = 0;
		for (int i = 0; i < predict1.size(); i++) {
			if (predict1.get(i).getPredictedClass() != predict2.get(i).getPredictedClass()) {
				diffnum++;
			}
		}
		return 1.0 * diffnum / predict1.size();
	}
	
	public static List<FeaturePair> selectFeedback(List<FeaturePair> predict, int num) {
		// Select according to highest similarity and constraints-first.
		List<FeaturePair> copy = new ArrayList<FeaturePair>(predict.size());
		for (FeaturePair p: predict) {
			copy.add(new FeaturePair(p));
		}
		
		Collections.sort(copy, new Comparator<FeaturePair>() {

			public int compare(FeaturePair p1, FeaturePair p2) {
				if (p1.getPredictedClass() != p2.getPredictedClass()) {
					if (p1.getPredictedClass() == FeaturePair.NO_CONSTRAINT) {
						return -1;
					}
					if (p2.getPredictedClass() == FeaturePair.NO_CONSTRAINT) {
						return 1;
					}
				}
				return p1.getTotalSim() > p2.getTotalSim() ? 1 : (
						p1.getTotalSim() == p2.getTotalSim() ? 0 : -1);
			}
			
		});
		
		if (num > copy.size()) {
			num = copy.size();
		}
		
		return copy.subList(copy.size() - num, num);
	}
	
	public Prediction() {
		metrics.put(FeaturePair.REQUIRE, new Metric());
		metrics.put(FeaturePair.EXCLUDE, new Metric());
	}
	
	public double avgAccuracy() {
		return time == 0 ? 0 : accuracy / time;
	}
	
	public Metric getClassMetric(int classId) {
		return metrics.get(classId);
	}
	
	public void push(List<FeaturePair> pairs) {
		int correct = 0;
		int reqPositive = 0, reqTrue = 0, reqFalse = 0;
		int excPositive = 0, excTrue = 0, excFalse = 0;
		
		for (FeaturePair pair: pairs) {
			if (pair.getLabel() == FeaturePair.REQUIRE) {
				reqPositive++;
			}
			if (pair.getLabel() == FeaturePair.EXCLUDE) {
				excPositive++;
			}
			if (pair.getPredictedClass() == pair.getLabel()) {
				correct++;
				if (pair.getLabel() == FeaturePair.REQUIRE) {
					reqTrue++;
				}
				if (pair.getLabel() == FeaturePair.EXCLUDE) {
					excTrue++;
				}
			} else {
				if (pair.getPredictedClass() == FeaturePair.REQUIRE) {
					reqFalse++;
				}
				if (pair.getPredictedClass() == FeaturePair.EXCLUDE) {
					excFalse++;
				}
			}
		}
		
		this.time++;
		this.accuracy += 1.0 * correct / pairs.size();
		
		this.metrics.get(FeaturePair.REQUIRE).push(1.0 * reqTrue / (reqTrue + reqFalse), 
				1.0 * reqTrue / reqPositive);
		this.metrics.get(FeaturePair.EXCLUDE).push(1.0 * excTrue / (excTrue + excFalse),
				1.0 * excTrue / excPositive);
	}
	
	public void upmergeConstraints(List<FeaturePair> pairs) {
		firstMap.clear();
		secondMap.clear();
		for (FeaturePair pair: pairs) {
			firstMap.put(pair.getFirst().getId(), pair);
			secondMap.put(pair.getSecond().getId(), pair);
		}
		
		// For pair <A, B, Relation>, if <A's ancestor, B, Relation> or <A, B's ancestor, Relation>, 
		// or <A's ancestor, B's ancestor, Relation> exists, then <A, B, Relation> is removed.
		// (Here Relation = Excludes)
		for (FeaturePair pair: pairs) {
			if (pair.getPredictedClass() != FeaturePair.EXCLUDE) {
				continue;
			}
			List<FeaturePair> ancestorPair = calcAncestorPair(pair);
			for (FeaturePair ap: ancestorPair) {
				if (pair.getPredictedClass() == ap.getPredictedClass()) {
					pair.setPredictedClass(FeaturePair.NO_CONSTRAINT);
					break;
				}
			}
		}
	}

	private List<FeaturePair> calcAncestorPair(FeaturePair pair) {
		List<FeaturePair> result = new ArrayList<FeaturePair>();
		result.addAll(calcAncestorPair(pair.getFirst(), firstMap));
		result.addAll(calcAncestorPair(pair.getSecond(), secondMap));
		return result;
	}
	
	private List<FeaturePair> calcAncestorPair(Entity entity, Map<Long, FeaturePair> map) {
		List<Long> id = new ArrayList<Long>();
		getAncestorId(entity, id);
		
		List<FeaturePair> pair = new ArrayList<FeaturePair>();
		for (Long n: id) {
			pair.add(map.get(n));
		}
		return pair;
	}
	
	private void getAncestorId(Entity entity, List<Long> result) {
		for (Relation r: entity.getRels()) {
			if (!(r instanceof BinRelation)) {
				continue;
			}
			// Find en's parents
			BinRelation br = (BinRelation) r;
			if (br.getTargetId().equals(entity.getId())
					&& FeaturePair.isRefine(br)) {
				result.add(br.getSourceId());
				
				try {
					Entity parent = DaoUtil.getEntityDao().getById(br.getSourceId(), true);
					getAncestorId(parent, result);
				} catch (ItemPersistenceException e) {
					logger.warn("Fail to get ancestor.", e);
				} catch (StaleDataException e) {
					logger.warn("Fail to get ancestor.", e);
				}
			}
		}
	}
	
}
