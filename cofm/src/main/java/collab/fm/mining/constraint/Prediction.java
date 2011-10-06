package collab.fm.mining.constraint;

import java.util.ArrayList;
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
