package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.BinaryRelationship;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.exception.BeanPersistenceException;

public interface RelationshipDao extends GenericDao<Relationship, Long> {
	
	public List<Relationship> getByExample(BinaryRelationship example) throws BeanPersistenceException;
}
