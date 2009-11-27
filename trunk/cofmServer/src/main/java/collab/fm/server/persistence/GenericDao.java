package collab.fm.server.persistence;

import java.util.Collection;
import java.util.List;

import collab.fm.server.util.exception.BeanPersistenceException;

public interface GenericDao<BeanType, IdType> {
	
	public BeanType getById(IdType id) throws BeanPersistenceException;
	
	public List<BeanType> getAll() throws BeanPersistenceException;
	
	/**
	 * 
	 * @param example
	 * @param like true if similarity comparison, false if exactly comparison.
	 * @return null if nothing matches
	 */
	public List<BeanType> getByExample(BeanType example, boolean like) throws BeanPersistenceException;
	
	/**
	 * Save entity into database, where the entity should not exist in the database before.
	 * @param entity
	 * @return
	 * @throws BeanPersistenceException
	 */
	public IdType save(BeanType entity) throws BeanPersistenceException;
	
	/**
	 * Save an ordered list of entities into database, where these entities should not exist before.<br/>
	 * NOTE: The operation is atomic, i.e. if one entity can't be saved, no entity will
	 * be saved.
	 * @param entities
	 * @return The generated ID of entities, with the same order in the entity list.
	 * @throws BeanPersistenceException
	 */
	public List<IdType> saveAll(List<BeanType> entities) throws BeanPersistenceException;
	
	/**
	 * Update a persisted object.
	 * @param entity
	 * @throws BeanPersistenceException
	 */
	public void update(BeanType entity) throws BeanPersistenceException;
	
	/**
	 * Update all entities in the collection (which means the persistence is unordered). <br/>
	 * NOTE: The operation is atomic, i.e. if one entity can't be updated, no entity will be updated. 
	 * @param entities
	 * @throws BeanPersistenceException
	 */
	public void updateAll(Collection<BeanType> entities) throws BeanPersistenceException;
	
}
