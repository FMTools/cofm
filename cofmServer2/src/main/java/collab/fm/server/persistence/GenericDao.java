package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface GenericDao<ItemType, IdType> {
	
	/**
	 * Get entity by id.
	 * @param id
	 * @param lock
	 * 		True if the get should block any concurrent write.
	 * @return null if no such ID existed.
	 * @throws ItemPersistenceException
	 */
	public ItemType getById(IdType id, boolean lock) 
		throws ItemPersistenceException, StaleDataException;
	
	/**
	 * Get all items of ItemType from the model with modelId
	 * @param modelId
	 * @return Item list, null if no item.
	 * @throws ItemPersistenceException
	 * @throws StaleDataException
	 */
	public List getAllOfModel(IdType modelId) throws ItemPersistenceException, StaleDataException;
	
	/**
	 * Get all items of ItemType from the whole database!
	 * @return Item list, null if no tiem.
	 * @throws ItemPersistenceException
	 * @throws StaleDataException
	 */
	public List getAll() throws ItemPersistenceException, StaleDataException;
	
	/**
	 * Save entity into database.
	 * @param item
	 * @return
	 * @throws ItemPersistenceException
	 */
	public ItemType save(ItemType item) throws ItemPersistenceException, StaleDataException;
	
	/**
	 * Delete an entity by ID.
	 * @param itemId the entity's ID
	 * @param modelId the ID of the model which contains the entity
	 * @return TODO
	 * @throws ItemPersistenceException
	 * @throws StaleDataException
	 */
	public void deleteById(IdType itemId) throws ItemPersistenceException, StaleDataException;
	
	/**
	 * Delete an entity.
	 * @param item
	 * @throws ItemPersistenceException
	 * @throws StaleDataException
	 */
	public void delete(ItemType item) throws ItemPersistenceException, StaleDataException;
}
