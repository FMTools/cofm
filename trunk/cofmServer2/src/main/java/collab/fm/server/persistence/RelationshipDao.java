package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.BinaryRelationship;
import collab.fm.server.bean.persist.Relationship;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface RelationshipDao extends GenericDao<Relationship, Long> {
	
	public List<Relationship> getByExample(Long modelId, BinaryRelationship example) throws EntityPersistenceException, StaleDataException;
}
