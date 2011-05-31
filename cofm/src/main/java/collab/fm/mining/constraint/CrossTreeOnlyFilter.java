package collab.fm.mining.constraint;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class CrossTreeOnlyFilter implements PairFilter {

	public boolean keepPair(FeaturePair pair, int mode) {
		// Keep cross-tree pair only (i.e. the two features has no ancestor-descendant relation
		
		List<Long> treeOfFirst = new ArrayList<Long>();
		treeOfFirst.add(pair.getFirst().getId());
		treeOfFirst.addAll(getDescendant(pair.getFirst()));
		treeOfFirst.addAll(getAncestor(pair.getFirst()));
		
		return !treeOfFirst.contains(pair.getSecond().getId());
	}
	
	private List<Long> getDescendant(Entity feature) {
		List<Long> des = new ArrayList<Long>();
		
		for (Relation r: feature.getRels()) {
			if (r instanceof BinRelation) {
				BinRelation br = (BinRelation) r;
				if (br.getSourceId().equals(feature.getId()) && FeaturePair.isRefine(br)) {
					des.add(br.getTargetId());
					try {
						Entity child = DaoUtil.getEntityDao().getById(br.getTargetId(), false);
						des.addAll(getDescendant(child));
					} catch (ItemPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (StaleDataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return des;
	}
	
	private List<Long> getAncestor(Entity feature) {
		List<Long> anc = new ArrayList<Long>();
		
		for (Relation r: feature.getRels()) {
			if (r instanceof BinRelation) {
				BinRelation br = (BinRelation) r;
				if (br.getTargetId().equals(feature.getId()) && FeaturePair.isRefine(br)) {
					anc.add(br.getSourceId());
					try {
						Entity parent = DaoUtil.getEntityDao().getById(br.getSourceId(), false);
						anc.addAll(getAncestor(parent));
					} catch (ItemPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (StaleDataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return anc;
	}

}
