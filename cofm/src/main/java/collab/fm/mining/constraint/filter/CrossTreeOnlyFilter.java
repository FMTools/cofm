package collab.fm.mining.constraint.filter;

import java.util.ArrayList;
import java.util.List;

import collab.fm.mining.constraint.FeaturePair;
import collab.fm.mining.constraint.PairFilter;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class CrossTreeOnlyFilter implements PairFilter {

	public boolean keepPair(FeaturePair pair) {
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
			if (r.isRefine()) {
				if (r.containsParent(feature)) {
					List<Long> children = r.getChildrenId();
					des.addAll(children);
					try {
						for (Long c: children) {
							Entity child = DaoUtil.getEntityDao().getById(c, false);
							des.addAll(getDescendant(child));
						}
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
			if (r.isRefine()) {
				if (r.containsChild(feature)) {
					long parent = r.getParentId();
					anc.add(parent);
					try {
						Entity p = DaoUtil.getEntityDao().getById(parent, false);
						anc.addAll(getAncestor(p));
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

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
