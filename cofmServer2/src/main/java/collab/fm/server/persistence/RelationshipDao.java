package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface RelationshipDao extends GenericDao<Relation, Long> {
	
	public List<Relation> getByExample(Long modelId, BinRelation example) throws EntityPersistenceException, StaleDataException;
}
