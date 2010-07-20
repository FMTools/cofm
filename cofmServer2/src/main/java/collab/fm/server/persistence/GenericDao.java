package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface GenericDao<EntityType, IdType> {
	
	/**
	 * Get entity by id.
	 * @param id
	 * @param lock
	 * 		True if the get should block any concurrent write.
	 * @return null if no such ID existed.
	 * @throws EntityPersistenceException
	 */
	public EntityType getById(IdType id, boolean lock) 
		throws EntityPersistenceException, StaleDataException;
	
	public List getAll(IdType modelId) throws EntityPersistenceException, StaleDataException;
	
	/**
	 * Save entity into database.
	 * @param entity
	 * @return
	 * @throws EntityPersistenceException
	 */
	public EntityType save(EntityType entity) throws EntityPersistenceException, StaleDataException;
	
	/**
	 * Save an ordered list of entities into database.<br/>
	 * NOTE: The operation is atomic, i.e. if one entity can't be saved, no entity will
	 * be saved.
	 * @param entities
	 * @return The generated ID of entities, with the same order in the entity list.
	 * @throws EntityPersistenceException
	 */
	public List<EntityType> saveAll(List<EntityType> entities) throws EntityPersistenceException, StaleDataException;
	
	/**
	 * Delete an entity by ID.
	 * @param entityId the entity's ID
	 * @param modelId the ID of the model which contains the entity
	 * @return TODO
	 * @throws EntityPersistenceException
	 * @throws StaleDataException
	 */
	public void deleteById(IdType entityId) throws EntityPersistenceException, StaleDataException;
	
	/**
	 * Delete an entity.
	 * @param entity
	 * @throws EntityPersistenceException
	 * @throws StaleDataException
	 */
	public void delete(EntityType entity) throws EntityPersistenceException, StaleDataException;
}
