package collab.fm.server.persistence;

import java.util.Collection;
import java.util.List;

import collab.fm.server.util.exception.BeanPersistenceException;

public interface GenericDao<EntityType, IdType> {
	
	/**
	 * Get entity by id.
	 * @param id
	 * @param lock
	 * 		True if the get should block any concurrent write.
	 * @return null if no such ID existed.
	 * @throws BeanPersistenceException
	 */
	public EntityType getById(IdType id, boolean lock) throws BeanPersistenceException;
	
	public List<EntityType> getAll() throws BeanPersistenceException;
	
	/**
	 * 
	 * @param example
	 * @param like true if similarity comparison, false if exactly comparison.
	 * @return null if nothing matches
	 */
	public List<EntityType> getByExample(EntityType example, boolean like) throws BeanPersistenceException;
	
	/**
	 * Save entity into database.
	 * @param entity
	 * @return
	 * @throws BeanPersistenceException
	 */
	public EntityType save(EntityType entity) throws BeanPersistenceException;
	
	/**
	 * Save an ordered list of entities into database.<br/>
	 * NOTE: The operation is atomic, i.e. if one entity can't be saved, no entity will
	 * be saved.
	 * @param entities
	 * @return The generated ID of entities, with the same order in the entity list.
	 * @throws BeanPersistenceException
	 */
	public List<EntityType> saveAll(List<EntityType> entities) throws BeanPersistenceException;
	
}
