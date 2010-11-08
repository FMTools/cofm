package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface RelationDao extends GenericDao<Relation, Long> {
	
	public List<Relation> getByExample(Long modelId, BinRelation example) throws ItemPersistenceException, StaleDataException;
}
