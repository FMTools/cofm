package collab.fm.server.bean.query;

import java.util.List;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.entity.ValueList;
import collab.fm.server.bean.persist.relation.RelationType;

/**
 * The difference between the Query and the DAO (Data Access Object, see server.persistence package)
 * is that DAOs know only the type/name of fields of the beans but not the value/meaning of these fields.
 * That's why the methods such as getEntityByName belong to IQuery but not the DAO classes,
 * because to implement such a method, one should know that there is a default attribute standing 
 * for the Name for each entity; in contrast, the DAO only knows that there is a collection of
 * attributes for each entity, but do not know the actual attributes for any entity instance.
 * 
 * @author Yi Li
 *
 */
public interface IQuery {
	
	public List<Entity> getEntityByName(Long modelId, Long entityTypeId, String name);
	
	public EntityType getEntityTypeByName(Long modelId, String name);
	
	public RelationType getRelationTypeByName(Long modelId, String name);
	
	public List<RelationType> getInvolvedRelationTypeByEntityType(Long entityTypeId);
	
	public List<Entity> getParents(Long entityId);
	public List<Entity> getChildren(Long entityId);
	
	public List<Entity> getInvolvedEntities(Long entityId, String relationTypeName);
	
	public ValueList getPossibleValues(Long entityId, String attrName);
}
